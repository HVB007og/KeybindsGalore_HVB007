package net.hvb007.keybindsgalore.api;

public record ApiVersion(int major, int minor) {
    public ApiVersion {
        if (major < 0 || minor < 0) {
            throw new IllegalArgumentException("API version components must be non-negative");
        }
    }

    public static ApiVersion current() {
        return new ApiVersion(1, 0);
    }

    public boolean accepts(ApiVersion requested) {
        return requested.major == major && requested.minor <= minor;
    }
}
