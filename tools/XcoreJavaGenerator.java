package tools;

import static org.fusesource.jansi.Ansi.ansi;
import static tools.VanillaXcoreGenerator.MODEL_DIRECTORY_PLACEHOLDER;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.xcore.XcoreStandaloneSetup;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

public class XcoreJavaGenerator {

  /** Sorts issues by file name, line number and column */
  protected static final Comparator<Issue> ISSUE_COMPARATOR =
      Comparator.comparing(
          (Issue issue) ->
              issue.getUriToProblem().toString() + issue.getLineNumber() + issue.getColumn());

  protected final VanillaXcoreGenerator generator;
  protected final IResourceValidator validator;
  protected final Provider<XtextResourceSet> resourceSetProvider;
  protected final Map<String, URI> platformResourceMap = EcorePlugin.getPlatformResourceMap();

  @Inject
  public XcoreJavaGenerator(
      final VanillaXcoreGenerator generator,
      final IResourceValidator validator,
      final Provider<XtextResourceSet> resourceSetProvider) {
    this.generator = generator;
    this.validator = validator;
    this.resourceSetProvider = resourceSetProvider;
  }

  /**
   * Validates and generates model Java sources for the list of {@code *.xcore} files given with
   * {@code srcs}. The resulting Java sources will be written to a JAR file at {@code out}.
   *
   * @param srcs a list of paths to `*.xcore` files to process
   * @param out a path to the JAR file to write the generated sources to
   * @throws ValidationException when a Xcore file contains validation issues
   */
  public void validateAndGenerate(final List<? extends Path> srcs, final Path out)
      throws ValidationException {
    final var resourceSet = resourceSetProvider.get();

    // Load all sources into the resource set first, they may depend on each other
    final var resources =
        srcs.stream()
            .map(src -> URI.createFileURI(src.toString()))
            .map(uri -> resourceSet.getResource(uri, true))
            .toList();

    // Validate
    resources.forEach(
        resource -> {
          final var issues = validator.validate(resource, CheckMode.ALL, null);
          if (!issues.isEmpty()) {
            throw new ValidationException("Validation failed.", issues);
          }
        });

    // Generate
    final var outputDirectory = URI.createURI("archive:" + out.toUri() + "!/");
    synchronized (platformResourceMap) {
      platformResourceMap.put(MODEL_DIRECTORY_PLACEHOLDER, outputDirectory);
      resources.forEach(resource -> generator.doGenerate(resource, null));
    }
  }

  public static void main(final String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: XcoreJavaGenerator TARGET_JAR [XCORES...]");
      System.exit(1);
    }

    final var out = Path.of(args[0]);
    final var srcs = Arrays.stream(args, 1, args.length).map(Path::of).toList();

    final var injector = new XcoreStandaloneSetup().createInjectorAndDoEMFRegistration();
    final var generator = injector.getInstance(XcoreJavaGenerator.class);
    try {
      generator.validateAndGenerate(srcs, out);
    } catch (final ValidationException e) {
      e.getIssues().stream()
          .sorted(ISSUE_COMPARATOR)
          .forEachOrdered(issue -> writeIssue(System.out, issue));
      System.exit(1);
    }
  }

  public static class ValidationException extends RuntimeException {
    final List<Issue> issues;

    public ValidationException(final String message, final List<Issue> issues) {
      super(message);
      this.issues = issues;
    }

    public List<Issue> getIssues() {
      return issues;
    }
  }

  /**
   * Writes an issue to an output print stream in a nicely formatted way.
   *
   * @param out where to write the issue to
   * @param issue the issue to write
   * @throws UncheckedIOException upon I/O errors reading the source file
   */
  public static void writeIssue(final PrintStream out, final Issue issue) {
    final var fileName = issue.getUriToProblem().toFileString();
    final var severityColor =
        switch (issue.getSeverity()) {
          case ERROR -> Color.RED;
          case WARNING -> Color.YELLOW;
          case INFO -> Color.GREEN;
          case IGNORE -> Color.DEFAULT;
        };
    final var severityFmt =
        switch (issue.getSeverity()) {
          case ERROR, WARNING -> Attribute.INTENSITY_BOLD;
          case INFO, IGNORE -> Attribute.ITALIC;
        };

    // Print a subject line in the format
    // path/to/file.xcore:42:3: <SEVERITY>: <MESSAGE>
    out.print(
        ansi(256)
            .fgCyan()
            .a(fileName)
            .reset()
            .format(":%d:%d: ", issue.getLineNumber(), issue.getColumn())
            .fg(severityColor)
            .a(severityFmt)
            .a(issue.getSeverity())
            .reset()
            .a(": ")
            .a(issue.getMessage())
            .newline());

    if (issue.getLineNumber() > 0 && issue.getColumn() > 0) {
      // Print the erroneous line(s)
      final var path = Path.of(fileName);
      try (final var lines = Files.lines(path)) {
        final var skip = issue.getLineNumber() - 1;
        final var limit = issue.getLineNumberEnd() > 0 ? issue.getLineNumberEnd() - skip : 1;
        lines.skip(skip).limit(limit).forEach(out::println);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }

      // Highlight the erroneous column(s)
      out.print(
          ansi(128)
              .a(" ".repeat(issue.getColumn() - 1))
              .fgGreen()
              .a('^')
              .a("~".repeat(issue.getLength() > 0 ? issue.getLength() - 1 : 0))
              .newline()
              .newline());
    }
  }
}
