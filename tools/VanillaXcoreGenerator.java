package tools;

import static org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE;

import java.util.List;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory.Descriptor;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory.Descriptor.DelegatingRegistry;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xcore.generator.XcoreGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess;

/**
 * An {@link XcoreGenerator} that does not use Xtext's {@link IFileSystemAccess} API but EMF's
 * {@link URI#createPlatformResourceURI(String, boolean) platform URI mechanism} instead.
 *
 * <p>Make sure to register a target URI for the generator in {@link
 * org.eclipse.emf.ecore.plugin.EcorePlugin#getPlatformResourceMap()} for the key {@value
 * #MODEL_DIRECTORY_PLACEHOLDER}.
 *
 * <p>Example:
 *
 * <blockquote>
 *
 * <pre>
 * var targetDir = URI.createURI("path/to/target/directory");
 * EcorePlugin.getPlatformResourceMap().put(MODEL_DIRECTORY_PLACEHOLDER, targetDir);
 * vanillaXcoreGenerator.doGenerate(someResource, null);
 * </pre>
 *
 * </blockquote>
 */
public class VanillaXcoreGenerator extends XcoreGenerator {

  /**
   * A placeholder used as project name for created platform URIs. Register the actual target
   * location using this key in the {@link
   * org.eclipse.emf.ecore.plugin.EcorePlugin#getPlatformResourceMap() platform resource map}.
   */
  public static final String MODEL_DIRECTORY_PLACEHOLDER = "__MODEL_DIRECTORY__";

  /**
   * Generates model project sources for the given generator model.
   *
   * @param genModel the generator model to generate sources for
   * @param ignored ignored
   * @return the generator's result diagnostic
   */
  @Override
  public Diagnostic generateGenModel(final GenModel genModel, final IFileSystemAccess ignored) {
    genModel.setModelDirectory(MODEL_DIRECTORY_PLACEHOLDER);

    final var generator = new Generator(new GenModelAdapterRegistry());
    generator.setInput(genModel);

    return generator.generate(genModel, MODEL_PROJECT_TYPE, new BasicMonitor());
  }

  static class GenModelAdapterRegistry extends DelegatingRegistry {
    @Override
    protected List<Descriptor> getDescriptors(final String packageID, final boolean forceCreate) {
      return List.of(GenModelGeneratorAdapterFactory.DESCRIPTOR);
    }
  }
}
