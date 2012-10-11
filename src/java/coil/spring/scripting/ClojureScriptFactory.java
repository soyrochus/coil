package coil.spring.scripting;

import java.io.IOException;
import java.io.StringReader;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import clojure.lang.Compiler;

/**
 */
public class ClojureScriptFactory implements ScriptFactory, BeanClassLoaderAware {

    private final String scriptSourceLocator;
    
    private Object scriptResult;

    private final Object scriptClassMonitor = new Object();

    public ClojureScriptFactory(String scriptSourceLocator) {
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
                   
                   scriptResult = Compiler.load(new StringReader(scriptSource.getScriptAsString()));     

                }
            }

            return getScriptResult();
        }
        catch (Exception e) {
            throw new ScriptCompilationException("Could not exec Clojure script: " + scriptSource, e);
        }
    }

    public boolean requiresScriptedObjectRefresh(org.springframework.scripting.ScriptSource scriptSource){
        synchronized (getScriptClassMonitor()) {

            return (getScriptResult() == null || scriptSource.isModified());
        }
    }
    
}
