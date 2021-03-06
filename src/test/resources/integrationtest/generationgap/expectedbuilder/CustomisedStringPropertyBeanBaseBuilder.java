package integrationtest.generationgap.generatedbuilder;

public class CustomisedStringPropertyBeanBaseBuilder<T extends integrationtest.generationgap.generatedbuilder.CustomisedStringPropertyBeanBaseBuilder<T>> implements integrationtest.generationgap.generatedbuilder.Builder<uk.co.buildergenerator.testmodel.CustomisedStringPropertyBean> {
    
    private uk.co.buildergenerator.testmodel.CustomisedStringPropertyBean target = new uk.co.buildergenerator.testmodel.CustomisedStringPropertyBean();
    
    public CustomisedStringPropertyBeanBaseBuilder() {}
    
    public T withTheString(java.lang.String theString) {
        getTarget().setTheString(theString);
        return (T) this;
    }
    
    protected uk.co.buildergenerator.testmodel.CustomisedStringPropertyBean getTarget() {
        return target;
    }
    
    public uk.co.buildergenerator.testmodel.CustomisedStringPropertyBean build() {
        return getTarget();
    }
}
