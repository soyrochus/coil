package coil.spring.scripting;

import java.util.ArrayList;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.List;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Base class for Bean definitions.  Adapted from the {@link org.springframework.scripting.config.ScriptBeanDefinitionParser}.
 * 
 * @author jason
 */
public class RhinoBeanDefinitionParser extends AbstractBeanDefinitionParser {

    /**
     * The unique name under which the internally managed {@link ScriptFactoryPostProcessor} is
     * registered in the {@link BeanDefinitionRegistry}.
     */
    private static final String SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME = ".scriptFactoryPostProcessor";

    private static final String SCRIPT_SOURCE_ATTRIBUTE = "script-source";

    private static final String INLINE_SCRIPT_ELEMENT = "inline-script";

    private static final String REFRESH_CHECK_DELAY_ATTRIBUTE = "refresh-check-delay";
    
    public RhinoBeanDefinitionParser() {
        
    }
    
    
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        // Set up infrastructure.
        registerScriptFactoryPostProcessorIfNecessary(parserContext.getRegistry());
        RootBeanDefinition beanDefinition = new RootBeanDefinition(getFactoryClass());
        beanDefinition.setSource(parserContext.extractSource(element));

        // Attach any refresh metadata.
        parseRefreshMetadata(element, beanDefinition);

        // Add constructor arguments.
        ConstructorArgumentValues cav = beanDefinition.getConstructorArgumentValues();

        List<Object> args = parseAttributes(element, parserContext);
        for (int i = 0; i < args.size(); i++) {
            cav.addIndexedArgumentValue(i, args.get(i));
        }
        
        // Add any property definitions that need adding.
        parserContext.getDelegate().parsePropertyElements(element, beanDefinition);

        return beanDefinition;
    }

    /**
     * Parses the value of the '<code>refresh-check-delay</code>' attribute and
     * attaches it to the BeanDefinition metadata.
     * @see ScriptFactoryPostProcessor#REFRESH_CHECK_DELAY_ATTRIBUTE
     */
    private void parseRefreshMetadata(Element element, RootBeanDefinition beanDefinition) {
        String refreshCheckDelay = element.getAttribute(REFRESH_CHECK_DELAY_ATTRIBUTE);
        if (StringUtils.hasText(refreshCheckDelay)) {
            beanDefinition.setAttribute(ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE, new Long(refreshCheckDelay));
        }
    }

    /**
     * Resolves the script source from either the '<code>script-source</code>' attribute or
     * the '<code>inline-script</code>' element. Logs and {@link XmlReaderContext#error} and
     * returns <code>null</code> if neither or both of these values are specified.
     */
    private String resolveScriptSource(Element element, XmlReaderContext readerContext) {
        boolean hasScriptSource = element.hasAttribute(SCRIPT_SOURCE_ATTRIBUTE);
        List elements = DomUtils.getChildElementsByTagName(element, INLINE_SCRIPT_ELEMENT);
        if (hasScriptSource && !elements.isEmpty()) {
            readerContext.error("Only one of 'script-source' and 'inline-script' should be specified.", element);
            return null;
        }
        else if (hasScriptSource) {
            return element.getAttribute(SCRIPT_SOURCE_ATTRIBUTE);
        }
        else if (!elements.isEmpty()) {
            Element inlineElement = (Element) elements.get(0);
            return "inline:" + DomUtils.getTextValue(inlineElement);
        }
        else {
            readerContext.error("Must specify either 'script-source' or 'inline-script'.", element);
            return null;
        }
    }

    /**
     * Registers a {@link ScriptFactoryPostProcessor} bean definition in the supplied
     * {@link BeanDefinitionRegistry} if the {@link ScriptFactoryPostProcessor} hasn't
     * already been registered.
     */
    private static void registerScriptFactoryPostProcessorIfNecessary(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(ScriptFactoryPostProcessor.class);
            registry.registerBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME, beanDefinition);
        }
    }
    
    
    protected Class getFactoryClass() {
        return RhinoScriptFactory.class;
    }
    

    /**
     * Parse attributes in the definition.  Subclasses should extend and add their own attributes.
     *  
     */
    protected List<Object> parseAttributes(Element element, ParserContext parserContext) {
        List<Object> args = new ArrayList<Object>();
        // Resolve the script source.
        String value = resolveScriptSource(element, parserContext.getReaderContext());
        if (value == null) {
            return null;
        }
        
        /*String beanName = element.getAttribute("bean-name");
        if (beanName == null) {
            return null;
        }*/

        /*String returnType = element.getAttribute("return-type");
        Class returnTypeClass = null;
        if (returnType != null) {
            try {
                returnTypeClass = Class.forName(returnType);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
         }*/

        args.add(value);
        
        return args;
    }

    
}
