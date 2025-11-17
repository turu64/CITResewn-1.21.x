package shcm.shsupercm.fabric.citresewn.config;

import net.minecraft.util.Identifier;

/**
 * Broken paths are resourcepack file paths that do not follow {@link Identifier}'s specifications.<br>
 * NOTE: This feature is disabled in Minecraft 1.21.4+ due to API changes.
 * The config option remains for backwards compatibility but has no effect.
 * @see CITResewnConfig#broken_paths
 * @see CITResewnMixinConfiguration#broken_paths
 */
public class BrokenPaths {
    /**
     * When enabled, {@link Identifier}s will not check for their path's validity.
     * NOTE: This feature is disabled in Minecraft 1.21.4+ due to API changes.
     */
    public static boolean processingBrokenPaths = false;
}
