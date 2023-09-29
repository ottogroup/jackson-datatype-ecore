package tools;

import org.eclipse.emf.ecore.xcore.generator.XcoreGeneratorImpl;

public class Dummy {
  public static void main(final String[] args) {
    // The reference to XcoreGeneratorImpl triggers all external deps to be listed for IntelliJ
    System.out.println(XcoreGeneratorImpl.class);
  }
}
