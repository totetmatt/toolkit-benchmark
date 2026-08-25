package org.gephi.toolkit.benchmark.layout;

import java.security.CodeSource;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Proves that the maven-shade relocation set up in the layout-plugin-snapshot module lets two
 * different builds of org.gephi.layout.plugin.forceAtlas.ForceAtlas coexist on the same classpath:
 * the one bundled inside gephi-toolkit (stable) and the snapshot layout-plugin build, relocated
 * under the "snapshot" package prefix.
 */
public class ShadingValidationTest {

    @Test
    public void bothForceAtlasBuildsCoexistOnTheClasspath() {
        // From gephi-toolkit (stable)
        org.gephi.layout.plugin.forceAtlas.ForceAtlas stableVersion =
                new org.gephi.layout.plugin.forceAtlas.ForceAtlas();

        // From the snapshot layout-plugin build
        snapshot.org.gephi.layout.plugin.forceAtlas.ForceAtlas snapshotVersion =
                new snapshot.org.gephi.layout.plugin.forceAtlas.ForceAtlas();

        assertNotEquals(stableVersion.getClass().getName(), snapshotVersion.getClass().getName());

        // getName() resolves a Bundle.properties message via NbBundle, relative to the class's
        // package - exercising that resource relocation (not just class relocation) worked too.
        assertNotNull(stableVersion.getName());
        assertNotNull(snapshotVersion.getName());

        System.out.println("gephi-toolkit ForceAtlas (stable): " + stableVersion.getClass().getName()
                + " loaded from " + codeSourceOf(stableVersion.getClass()));
        System.out.println("layout-plugin ForceAtlas (snapshot): " + snapshotVersion.getClass().getName()
                + " loaded from " + codeSourceOf(snapshotVersion.getClass()));
    }

    private static String codeSourceOf(Class<?> clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        return codeSource == null ? "unknown" : codeSource.getLocation().toString();
    }
}
