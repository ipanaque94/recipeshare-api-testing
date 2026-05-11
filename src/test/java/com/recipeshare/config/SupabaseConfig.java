package com.recipeshare.config;

import java.io.InputStream;
import java.util.Properties;

public class SupabaseConfig {
    private static final Properties props = new Properties();

    static {
        try {
            InputStream is = SupabaseConfig.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");
            if (is == null) {
                throw new RuntimeException(
                        "config.properties no encontrado");
            }
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando configuración: " + e.getMessage());
        }
    }

    public static String getBaseUrl() {
        return props.getProperty("base.url");
    }

    public static String getApiKey() {
        return props.getProperty("supabase.apikey");
    }

    public static String getEmailValido() {
        return props.getProperty("email.valido");
    }

    public static String getPasswordValido() {
        return props.getProperty("password.valido");
    }

    public static String getRecipeIdExistente() {
        return props.getProperty("recipe.id.existente");
    }

    public static String getUserId() {
        return props.getProperty("user.id");
    }
}

