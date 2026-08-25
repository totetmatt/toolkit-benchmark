package org.gephi.toolkit.benchmark;

import java.security.CodeSource;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Proves that the maven-shade relocation set up in the layout-plugin-relocated module lets two
 * different builds of org.gephi.layout.plugin.forceAtlas.ForceAtlas coexist on the same classpath:
 * the one bundled inside gephi-toolkit (0.11.2) and the local layout-plugin build (0.11.3-SNAPSHOT),
 * relocated under the "shaded.layoutplugin0113" package prefix.
 */
public class ShadingValidationTest {

    @Test
    public void bothForceAtlasBuildsCoexistOnTheClasspath() {
        // From gephi-toolkit stable
        org.gephi.layout.plugin.forceAtlas.ForceAtlas toolkitVersion =
                new org.gephi.layout.plugin.forceAtlas.ForceAtlas();

        // From the local layout-plugin
       local.org.gephi.layout.plugin.forceAtlas.ForceAtlas localVersion =
                new local.org.gephi.layout.plugin.forceAtlas.ForceAtlas();

        assertNotEquals(toolkitVersion.getClass().getName(), localVersion.getClass().getName());

        // getName() resolves a Bundle.properties message via NbBundle, relative to the class's
        // package - exercising that resource relocation (not just class relocation) worked too.
        assertNotNull(toolkitVersion.getName());
        assertNotNull(localVersion.getName());

        System.out.println("gephi-toolkit ForceAtlas: " + toolkitVersion.getClass().getName()
                + " loaded from " + codeSourceOf(toolkitVersion.getClass()));
        System.out.println("local layout-plugin ForceAtlas: " + localVersion.getClass().getName()
                + " loaded from " + codeSourceOf(localVersion.getClass()));
    }

    private static String codeSourceOf(Class<?> clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        return codeSource == null ? "unknown" : codeSource.getLocation().toString();
    }
}
