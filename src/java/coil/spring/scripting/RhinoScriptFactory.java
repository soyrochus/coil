package coil.spring.scripting;

import java.io.IOException;
import java.io.StringReader;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;


/**
 */
public class RhinoScriptFactory implements ScriptFactory, BeanClassLoaderAware {

    private final String scriptSourceLocator;
    
    private Object scriptResult;

    private final Object scriptClassMonitor = new Object();

    public RhinoScriptFactory(String scriptSourceLocator) {
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        
        this.scriptSourceLocator = scriptSourceLocator;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
    }

    public String getScriptSourceLocator() {
        return this.scriptSourceLocator;
    }

    public Class[] getScriptInterfaces() {
        return null;
    }

    public boolean requiresConfigInterface() {
        return false;
    }

    public Class getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        synchronized (this.scriptClassMonitor) {
            if (this.scriptResult == null || scriptSource.isModified()) {
                return null;
            }
            return this.scriptResult.getClass();
        }
    }
    
    protected Object getScriptResult() {
        return scriptResult;
    }
    
    protected void setScriptResult(Object scriptResult) {
        this.scriptResult = scriptResult;
    }
    
    
    protected Object getScriptClassMonitor() {
        return scriptClassMonitor;
    }

    public Object getScriptedObject(ScriptSource scriptSource, Class[] actualInterfaces) throws IOException, ScriptCompilationException {
        try {
            synchronized (getScriptClassMonitor()) {
                if (getScriptResult() == null || scriptSource.isModified()) {
                                       
                    // Create and enter a Context. A Context stores information about the execution environment of a script.                   
                    Context cx = Context.enter();
                    Object obj = null;
        
                    try
                        {
                            // Initialize the standard objects (Object, Function, etc.). This must be done before scripts can be
                            // executed. The null parameter tells initStandardObjects 
                            // to create and return a scope object that we use
                            // in later calls.
                            Scriptable scope = cx.initStandardObjects();            

                            obj= cx.evaluateReader(scope, new StringReader(scriptSource.getScriptAsString()) , "RhinoScript", 1, null );
            
                            if (obj instanceof Wrapper){
                                obj = ((Wrapper)obj).unwrap();
                            }
                            
                            scriptResult = obj;
                        }
                    finally
                        {
                            // Exit the Context. This removes the association between the Context and the current thread and is an
                            // essential cleanup action. There should be a call to exit for every call to enter.
                            Context.exit();
                        }

                }
            }

            return getScriptResult();
        }
        catch (Exception e) {
            throw new ScriptCompilationException("Could not exec Rhino script: " + scriptSource, e);
        }
    }

    public boolean requiresScriptedObjectRefresh(org.springframework.scripting.ScriptSource scriptSource){
        synchronized (getScriptClassMonitor()) {

            return (getScriptResult() == null || scriptSource.isModified());
        }
    }
    
}
