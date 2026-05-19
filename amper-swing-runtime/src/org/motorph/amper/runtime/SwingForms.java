package org.motorph.amper.runtime;

import java.lang.reflect.Method;

/**
 * Utility to initialize Swing UI forms using generated helpers.
 */
public class SwingForms {
    /**
     * Automatically discovers the FormHelper for the target object and initializes its UI.
     * Convention: If target class is com.example.MyForm, looks for com.example.MyFormFormHelper.
     *
     * @param target The object whose fields should be initialized (usually 'this' in a constructor).
     */
    public static void init(Object target) {
        if (target == null) return;
        
        String className = target.getClass().getName();
        String helperName = className + "FormHelper";
        
        try {
            Class<?> helperClass = Class.forName(helperName);
            Object instance = null;
            try {
                // Generated helpers are Kotlin objects, they have an 'INSTANCE' field.
                instance = helperClass.getField("INSTANCE").get(null);
            } catch (NoSuchFieldException e) {
                // Not a Kotlin object, or field missing
            }

            Method initMethod = helperClass.getMethod("initUI", Object.class);
            initMethod.invoke(instance, target);
        } catch (ClassNotFoundException e) {
            // Silently skip if no helper found, or log warning
            // System.out.println("No FormHelper found for " + className);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UI for " + className + 
                ". Ensure " + helperName + " is generated and all bound fields exist in the target class.", e);
        }
    }
}
