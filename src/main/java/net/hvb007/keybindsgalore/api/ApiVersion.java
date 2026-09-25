package net.hvb007.keybindsgalore.api;

/**
 * Contract version of the KeybindsGalore API.
 *
 * <p>The major component changes on a breaking change; the minor component increases
 * when members are added without breaking callers. Consumers should compare against
 * {@link #current()} before using newer members.
 *
 * @param major the incompatible version
 * @param minor the backwards-compatible revision
 */
public record ApiVersion(int major, int minor) {
    public ApiVersion {
        if (major < 0 || minor < 0) {
            throw new IllegalArgumentException("API version components must be non-negative");
        }
    }

    /**
     * Returns the version this build implements.
     *
     * @return the current API version
     */
    public static ApiVersion current() {
        return new ApiVersion(1, 0);
    }

    /**
     * Checks whether this version can satisfy a caller's requested version.
     *
     * @param requested the version the caller was written against
     * @return true when the major versions match and the minor is not newer than this one
     */
    public boolean accepts(ApiVersion requested) {
        return requested.major == major && requested.minor <= minor;
    }
}
