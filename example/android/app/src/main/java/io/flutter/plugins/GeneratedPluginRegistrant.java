package io.flutter.plugins;

import io.flutter.plugin.common.PluginRegistry;
import com.codepoka.call_with_whatsapp.CallWithWhatsappPlugin;

/**
 * Generated file. Do not edit.
 */
public final class GeneratedPluginRegistrant {
  public static void registerWith(PluginRegistry registry) {
    if (alreadyRegisteredWith(registry)) {
      return;
    }
    CallWithWhatsappPlugin.registerWith(registry.registrarFor("com.codepoka.call_with_whatsapp.CallWithWhatsappPlugin"));
  }

  private static boolean alreadyRegisteredWith(PluginRegistry registry) {
    final String key = GeneratedPluginRegistrant.class.getCanonicalName();
    if (registry.hasPlugin(key)) {
      return true;
    }
    registry.registrarFor(key);
    return false;
  }
}
