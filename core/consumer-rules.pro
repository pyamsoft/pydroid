# Remove anything marked with @RemoveInRelease annotation
-assumenosideeffects class ** {

  # Remove @RemovableInRelease methods so long as return values are unused.
  @com.pyamsoft.pydroid.core.RemoveInRelease <methods>;

  # Remove object @RemovableInRelease methods even when return value is used.
  # Note: * in return type does not match primitives.
  @com.pyamsoft.pydroid.core.RemoveInRelease * *(...) return null;

  # Remove boolean @RemovableInRelease methods even when return value is used.
  @com.pyamsoft.pydroid.core.RemoveInRelease boolean *(...) return false;
}

