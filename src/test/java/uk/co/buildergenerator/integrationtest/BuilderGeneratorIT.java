package uk.co.buildergenerator.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import uk.co.buildergenerator.BuilderGenerator;
import uk.co.buildergenerator.FileUtils;
import uk.co.buildergenerator.testmodel.Address;
import uk.co.buildergenerator.testmodel.ArrayOfNonJavaTypesPropertyWithSetArrayMethod;
import uk.co.buildergenerator.testmodel.ArrayOfPrimitiveIntsPropertyWithSetArrayMethod;
import uk.co.buildergenerator.testmodel.ArrayOfStringsPropertyWithSetArrayMethod;
import uk.co.buildergenerator.testmodel.Audi;
import uk.co.buildergenerator.testmodel.BeanToBeIgnored;
import uk.co.buildergenerator.testmodel.BeanWhereFieldNameDiffersFromBeanProperteyNameFromAccessors;
import uk.co.buildergenerator.testmodel.BeanWithAnInterfaceCollectionProperty;
import uk.co.buildergenerator.testmodel.BeanWithAnInterfaceProperty;
import uk.co.buildergenerator.testmodel.BeanWithChildBeanToBeIgnored;
import uk.co.buildergenerator.testmodel.BeanWithJodaTime;
import uk.co.buildergenerator.testmodel.BeanWithMapProperty;
import uk.co.buildergenerator.testmodel.BeanWithMultiDimensionalArrayOfPrimitives;
import uk.co.buildergenerator.testmodel.BeanWithNestedEnum;
import uk.co.buildergenerator.testmodel.BeanWithNonGenericCollections;
import uk.co.buildergenerator.testmodel.BeanWithNonWritableProperty;
import uk.co.buildergenerator.testmodel.BeanWithNullSubListInterfaceProperty;
import uk.co.buildergenerator.testmodel.BeanWithPropertyToIgnore;
import uk.co.buildergenerator.testmodel.BeanWithTopLevelEnumProperty;
import uk.co.buildergenerator.testmodel.BooleanPropertyBean;
import uk.co.buildergenerator.testmodel.BooleanPropertyBeanWithIsAndGetMethods;
import uk.co.buildergenerator.testmodel.BooleanPropertyBeanWithIsMethod;
import uk.co.buildergenerator.testmodel.CustomerCarBuilder;
import uk.co.buildergenerator.testmodel.CustomisedStringPropertyBean;
import uk.co.buildergenerator.testmodel.CyclicDependencyBeanLeft;
import uk.co.buildergenerator.testmodel.Ford;
import uk.co.buildergenerator.testmodel.InitialisedListPropertyWithAddMethod;
import uk.co.buildergenerator.testmodel.InitialisedListPropertyWithAddMethodAndSetListMethod;
import uk.co.buildergenerator.testmodel.InitialisedListPropertyWithSetListMethod;
import uk.co.buildergenerator.testmodel.InitialisedQueuePropertyWithAddMethod;
import uk.co.buildergenerator.testmodel.InitialisedSetPropertyWithAddMethod;
import uk.co.buildergenerator.testmodel.InitialisedSetPropertyWithSetSetMethod;
import uk.co.buildergenerator.testmodel.NullCollectionPropertyWithSetCollectionMethod;
import uk.co.buildergenerator.testmodel.NullLinkedListPropertyWithSetLinkedListMethod;
import uk.co.buildergenerator.testmodel.NullListOfBuilderTargetTypesPropertyWithSetListMethod;
import uk.co.buildergenerator.testmodel.NullListPropertyWithSetListMethod;
import uk.co.buildergenerator.testmodel.NullQueuePropertyWithSetQueueMethod;
import uk.co.buildergenerator.testmodel.NullSetPropertyWithSetSetMethod;
import uk.co.buildergenerator.testmodel.Root;
import uk.co.buildergenerator.testmodel.SelfReferencingBean;
import uk.co.buildergenerator.testmodel.StringPropertyBean;
import uk.co.buildergenerator.testmodel.StringPropertyBean2;
import uk.co.buildergenerator.testmodel.StringPropertyBeanWithSomethingElse;
import uk.co.buildergenerator.testmodel.StringPropertyBeanWithSomethingElseForGenerationGapTest;
import uk.co.buildergenerator.testmodel.SubClassOfInitialisedListPropertyWithAddMethod;
import uk.co.buildergenerator.testmodel.SubClassOfInitialisedListPropertyWithSetListMethod;

public class BuilderGeneratorIT {
    
    private static final String BUILDER_PACKAGE = "integrationtest.generatedbuilder";
    private static final String GENERATION_GAP_BUILDER_PACKAGE = "integrationtest.generationgap.generatedbuilder";
    private static final String OUTPUT_DIRECTORY = "./target/test-classes";
//    private static final String OUTPUT_DIRECTORY = "./src/test/java";

    private String readFile(String filename) throws IOException {
        
        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(filename);
        if (systemResourceAsStream == null) {
            fail(filename + " does not exist.");
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(systemResourceAsStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = r.readLine()) != null) {
        	// throw away trailing white space, but not leading for test failure readability
            line = StringUtils.stripEnd(line, null);
        	// throw away blank lines
            if (line.length() > 0) {
                sb.append(line + "\n");
            }
        }
        
        return sb.toString();
    }
    
    private void assertFilesEqual(String expectedBuilderFilename, String generatedBuilderFilename) throws IOException {

        String expected = readFile(expectedBuilderFilename);
        String actual = readFile(generatedBuilderFilename);
        assertEquals(expected, actual);
        
    }
    
    private BuilderGenerator createBuilderGenerator(Class<?> root, String builderPackage, String outputDirectory) {
    	return createBuilderGenerator(root, builderPackage, outputDirectory, null, null);
    }
    
    private BuilderGenerator createBuilderGenerator(Class<?> root, String builderPackage, String outputDirectory, Class<?> classToIgnoreProperty, String propertyToIgnore) {
        BuilderGenerator bg = new BuilderGenerator(root);
        bg.setBuilderPackage(builderPackage);
        bg.setOutputDirectory(outputDirectory);
        if (classToIgnoreProperty != null && propertyToIgnore != null) {
            bg.addPropertyToIgnore(classToIgnoreProperty, propertyToIgnore);
        }
        return bg;
    }

    @Test
    public void stringProperty() throws Exception {
        
        createBuilderGenerator(StringPropertyBean.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        String generatedBuilderFilename = "integrationtest/generatedbuilder/StringPropertyBeanBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/StringPropertyBeanBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
        assertFilesEqual("integrationtest/expectedbuilder/Builder.java", "integrationtest/generatedbuilder/Builder.java");
    }


    @Test
    public void primitiveBooleanProperty() throws Exception {
        
        createBuilderGenerator(BooleanPropertyBean.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BooleanPropertyBeanBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BooleanPropertyBeanBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void primitiveBooleanPropertyWithIsMethod() throws Exception {
        
        createBuilderGenerator(BooleanPropertyBeanWithIsMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BooleanPropertyBeanWithIsMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BooleanPropertyBeanWithIsMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void primitiveBooleanPropertyWithIsAndGetMethods() throws Exception {
        
        createBuilderGenerator(BooleanPropertyBeanWithIsAndGetMethods.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BooleanPropertyBeanWithIsAndGetMethodsBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BooleanPropertyBeanWithIsAndGetMethodsBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void beanGraphBuilderGeneration() throws Exception {

        createBuilderGenerator(Root.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();

        String generatedBuilderFilename = "integrationtest/generatedbuilder/RootBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/RootBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);

        generatedBuilderFilename = "integrationtest/generatedbuilder/NodeOneBuilder.java";
        expectedBuilderFilename = "integrationtest/expectedbuilder/NodeOneBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
        
        generatedBuilderFilename = "integrationtest/generatedbuilder/NodeTwoBuilder.java";
        expectedBuilderFilename = "integrationtest/expectedbuilder/NodeTwoBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
        
        generatedBuilderFilename = "integrationtest/generatedbuilder/NodeThreeBuilder.java";
        expectedBuilderFilename = "integrationtest/expectedbuilder/NodeThreeBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void cyclicBeanDependency() throws Exception {
        
        createBuilderGenerator(CyclicDependencyBeanLeft.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/CyclicDependencyBeanLeftBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/CyclicDependencyBeanLeftBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
        
        generatedBuilderFilename = "integrationtest/generatedbuilder/CyclicDependencyBeanRightBuilder.java";
        expectedBuilderFilename = "integrationtest/expectedbuilder/CyclicDependencyBeanRightBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void selfReferencingBean() throws Exception {

        createBuilderGenerator(SelfReferencingBean.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/SelfReferencingBeanBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/SelfReferencingBeanBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void listPropertyWithAddMethod() throws Exception {
        
        createBuilderGenerator(InitialisedListPropertyWithAddMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/InitialisedListPropertyWithAddMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/InitialisedListPropertyWithAddMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void listPropertyWithAddMethodAndSetListMethod() throws Exception {
        
        createBuilderGenerator(InitialisedListPropertyWithAddMethodAndSetListMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/InitialisedListPropertyWithAddMethodAndSetListMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/InitialisedListPropertyWithAddMethodAndSetListMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void initialisedListPropertyWithSetListMethod() throws Exception {
        
        createBuilderGenerator(InitialisedListPropertyWithSetListMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/InitialisedListPropertyWithSetListMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/InitialisedListPropertyWithSetListMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void subClassOfnitialisedListPropertyWithSetListMethod() throws Exception {
        
        createBuilderGenerator(SubClassOfInitialisedListPropertyWithSetListMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/SubClassOfInitialisedListPropertyWithSetListMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/SubClassOfInitialisedListPropertyWithSetListMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void subClassOfListPropertyWithAddMethod() throws Exception {
        
        createBuilderGenerator(SubClassOfInitialisedListPropertyWithAddMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/SubClassOfInitialisedListPropertyWithAddMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/SubClassOfInitialisedListPropertyWithAddMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void nullListPropertyWithSetListMethod() throws Exception {
        
        createBuilderGenerator(NullListPropertyWithSetListMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/NullListPropertyWithSetListMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/NullListPropertyWithSetListMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void nullCollectionPropertyWithSetCollectionMethod() throws Exception {
        
        createBuilderGenerator(NullCollectionPropertyWithSetCollectionMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/NullCollectionPropertyWithSetCollectionMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/NullCollectionPropertyWithSetCollectionMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void nullLinkedListPropertyWithSetLinkedListMethod() throws Exception {
        
        createBuilderGenerator(NullLinkedListPropertyWithSetLinkedListMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/NullLinkedListPropertyWithSetLinkedListMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/NullLinkedListPropertyWithSetLinkedListMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void setPropertyWithAddMethod() throws Exception {
        
        createBuilderGenerator(InitialisedSetPropertyWithAddMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/InitialisedSetPropertyWithAddMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/InitialisedSetPropertyWithAddMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void initialisedSetPropertyWithSetSetMethod() throws Exception {
        
        createBuilderGenerator(InitialisedSetPropertyWithSetSetMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/InitialisedSetPropertyWithSetSetMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/InitialisedSetPropertyWithSetSetMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void nullSetPropertyWithSetSetMethod() throws Exception {
        
        createBuilderGenerator(NullSetPropertyWithSetSetMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/NullSetPropertyWithSetSetMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/NullSetPropertyWithSetSetMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void initialisedQueuePropertyWithSetQueueMethod() throws Exception {
        
        createBuilderGenerator(InitialisedQueuePropertyWithAddMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/InitialisedQueuePropertyWithAddMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/InitialisedQueuePropertyWithAddMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void nullQueuePropertyWithSetQueueMethod() throws Exception {
        
        createBuilderGenerator(NullQueuePropertyWithSetQueueMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/NullQueuePropertyWithSetQueueMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/NullQueuePropertyWithSetQueueMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void arrayOfStringsPropertyWithSetArrayMethod() throws Exception {
        
        createBuilderGenerator(ArrayOfStringsPropertyWithSetArrayMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/ArrayOfStringsPropertyWithSetArrayMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/ArrayOfStringsPropertyWithSetArrayMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void arrayOfPrimitiveIntsPropertyWithSetArrayMethod() throws Exception {
        
        createBuilderGenerator(ArrayOfPrimitiveIntsPropertyWithSetArrayMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/ArrayOfPrimitiveIntsPropertyWithSetArrayMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/ArrayOfPrimitiveIntsPropertyWithSetArrayMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void arrayOfNonJavaTypesPropertyWithSetArrayMethod() throws Exception {
        
        createBuilderGenerator(ArrayOfNonJavaTypesPropertyWithSetArrayMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/ArrayOfNonJavaTypesPropertyWithSetArrayMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/ArrayOfNonJavaTypesPropertyWithSetArrayMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void beanWhereFieldNameDiffersFromBeanProperteyNameFromAccessors() throws Exception {
        
        createBuilderGenerator(BeanWhereFieldNameDiffersFromBeanProperteyNameFromAccessors.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWhereFieldNameDiffersFromBeanProperteyNameFromAccessorsBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWhereFieldNameDiffersFromBeanProperteyNameFromAccessorsBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void beanWithTopLevelEnumProperty() throws Exception {
        
        createBuilderGenerator(BeanWithTopLevelEnumProperty.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithTopLevelEnumPropertyBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithTopLevelEnumPropertyBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void beanWithNestedEnum() throws Exception {
        
        createBuilderGenerator(BeanWithNestedEnum.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithNestedEnumBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithNestedEnumBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void beanWithMultiDimensionalArrayOfPrimitives() throws Exception {
        
        createBuilderGenerator(BeanWithMultiDimensionalArrayOfPrimitives.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithMultiDimensionalArrayOfPrimitivesBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithMultiDimensionalArrayOfPrimitivesBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void beanWithNonWritableProperty() throws Exception {
        
        createBuilderGenerator(BeanWithNonWritableProperty.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithNonWritablePropertyBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithNonWritablePropertyBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void beanWithJodaTimeProperties() throws Exception {
        
        createBuilderGenerator(BeanWithJodaTime.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithJodaTimeBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithJodaTimeBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void nullListOfBuilderTargetTypesPropertyWithSetListMethod() throws Exception {
        
        createBuilderGenerator(NullListOfBuilderTargetTypesPropertyWithSetListMethod.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/NullListOfBuilderTargetTypesPropertyWithSetListMethodBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/NullListOfBuilderTargetTypesPropertyWithSetListMethodBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    
    @Test
    public void beanWithPropertyToIgnore() throws Exception {
        
        createBuilderGenerator(BeanWithPropertyToIgnore.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY, BeanWithPropertyToIgnore.class, "propertyToIgnore").generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithPropertyToIgnoreBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithPropertyToIgnoreBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void beanWithChildBeanToIgnore() throws Exception {
        
        BuilderGenerator builderGenerator = createBuilderGenerator(BeanWithChildBeanToBeIgnored.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.addClassToIgnore(BeanToBeIgnored.class);
        builderGenerator.generateBuilders();
        assertNoBuilderGenerated("integrationtest/generatedbuilder/BeanToBeIgnoredBuilder.java");
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithChildBeanToBeIgnoredBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithChildBeanToBeIgnoredBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    private void assertNoBuilderGenerated(String string) {
        File f = new File(OUTPUT_DIRECTORY, string);
        assertFalse("builder should not have been generated", f.exists());
    }

    @Test
    public void beanWithChildBeanToIgnoreUsingStringClassName() throws Exception {
        
        BuilderGenerator builderGenerator = createBuilderGenerator(BeanWithChildBeanToBeIgnored.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.addClassToIgnore(BeanToBeIgnored.class.getName());
        builderGenerator.generateBuilders();
        assertNoBuilderGenerated("integrationtest/generatedbuilder/BeanToBeIgnoredBuilder.java");
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithChildBeanToBeIgnoredBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithChildBeanToBeIgnoredBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void beanWithChildBeanToIgnoreUsingStringWithWildCard() throws Exception {
        
        BuilderGenerator builderGenerator = createBuilderGenerator(BeanWithChildBeanToBeIgnored.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.addClassToIgnore("uk.co.buildergenerator.testmodel.BeanToBeIgn*");
        builderGenerator.generateBuilders();
        assertNoBuilderGenerated("integrationtest/generatedbuilder/BeanToBeIgnoredBuilder.java");
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithChildBeanToBeIgnoredBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithChildBeanToBeIgnoredBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void beanWithAnInterfaceProperty() throws Exception {
        
        createBuilderGenerator(BeanWithAnInterfaceProperty.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithAnInterfacePropertyBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithAnInterfacePropertyBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }
    
    @Test
    public void beanWithAnInterfaceCollectionProperty() throws Exception {
        
        createBuilderGenerator(BeanWithAnInterfaceCollectionProperty.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithAnInterfaceCollectionPropertyBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithAnInterfaceCollectionPropertyBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void beanWithNonGenericCollectionsProperty() throws Exception {
        
        createBuilderGenerator(BeanWithNonGenericCollections.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithNonGenericCollectionsBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithNonGenericCollectionsBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void beanWithNullSubListInterfaceProperty() throws Exception {
        
        createBuilderGenerator(BeanWithNullSubListInterfaceProperty.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithNullSubListInterfacePropertyBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithNullSubListInterfacePropertyBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void stringPropertyBeanWithGenerationGap() throws Exception {
        
        BuilderGenerator builderGenerator = createBuilderGenerator(StringPropertyBean.class, GENERATION_GAP_BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.setGenerationGap(true);
        builderGenerator.generateBuilders();
        
        String generatedBaseBuilderFilename = "integrationtest/generationgap/generatedbuilder/StringPropertyBeanBaseBuilder.java";
        String expectedBaseBuilderFilename = "integrationtest/generationgap/expectedbuilder/StringPropertyBeanBaseBuilder.java";
        assertFilesEqual(expectedBaseBuilderFilename, generatedBaseBuilderFilename);
        
        String generatedBuilderFilename = "integrationtest/generationgap/generatedbuilder/StringPropertyBeanBuilder.java";
        String expectedBuilderFilename = "integrationtest/generationgap/expectedbuilder/StringPropertyBeanBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void generationGapWithCustomisedBuilder() throws Exception {
        
        // TODO: tidy this mess
        
        FileUtils fileUtils = new FileUtils();
        File outputDirectory = fileUtils.newFile(OUTPUT_DIRECTORY);
        fileUtils.createDirectoriesIfNotExists(outputDirectory);
        
        File packageDirectory = fileUtils.newFile(outputDirectory, "integrationtest/generationgap/generatedbuilder");
        fileUtils.createDirectoriesIfNotExists(packageDirectory);
        
        String generatedBuilderFilename = "CustomisedStringPropertyBeanBuilder.java";
        String expectedBuilderFilename = "integrationtest/generationgap/expectedbuilder/CustomisedStringPropertyBeanBuilder.java";
        File existingCustomisedBuilder = fileUtils.newFile(packageDirectory, generatedBuilderFilename);
        fileUtils.createFileIfNotExists(existingCustomisedBuilder);
        String readFile = readFile(expectedBuilderFilename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(existingCustomisedBuilder));
        writer.write(readFile);
        writer.flush();
        writer.close();
        
        BuilderGenerator builderGenerator = createBuilderGenerator(CustomisedStringPropertyBean.class, GENERATION_GAP_BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.setGenerationGap(true);
        builderGenerator.generateBuilders();
        
        String generatedBaseBuilderFilename = "integrationtest/generationgap/generatedbuilder/CustomisedStringPropertyBeanBaseBuilder.java";
        String expectedBaseBuilderFilename = "integrationtest/generationgap/expectedbuilder/CustomisedStringPropertyBeanBaseBuilder.java";
        assertFilesEqual(expectedBaseBuilderFilename, generatedBaseBuilderFilename);
        
        assertFilesEqual(expectedBuilderFilename, "integrationtest/generationgap/generatedbuilder/" + generatedBuilderFilename);
    }

    @Test
    public void generationGapWithDifferentPackageForBaseBuilders() throws Exception {
        
        BuilderGenerator builderGenerator = createBuilderGenerator(StringPropertyBean.class, GENERATION_GAP_BUILDER_PACKAGE + ".builder", OUTPUT_DIRECTORY);
        builderGenerator.setGenerationGap(true);
        builderGenerator.setGenerationGapBaseBuilderPackage(GENERATION_GAP_BUILDER_PACKAGE + ".base");
        builderGenerator.generateBuilders();
        
        String generatedBaseBuilderFilename = "integrationtest/generationgap/generatedbuilder/base/StringPropertyBeanBaseBuilder.java";
        String expectedBaseBuilderFilename = "integrationtest/generationgap/expectedbuilder/base/StringPropertyBeanBaseBuilder.java";
        assertFilesEqual(expectedBaseBuilderFilename, generatedBaseBuilderFilename);
        
        String generatedBuilderFilename = "integrationtest/generationgap/generatedbuilder/builder/StringPropertyBeanBuilder.java";
        String expectedBuilderFilename = "integrationtest/generationgap/expectedbuilder/base/StringPropertyBeanBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);

    }

    @Test
    public void generationGapWithDifferentOutputDirectoryForBaseBuilders() throws Exception {
        
        BuilderGenerator builderGenerator = createBuilderGenerator(StringPropertyBean2.class, GENERATION_GAP_BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.setGenerationGap(true);
        builderGenerator.setGenerationGapBaseBuilderOutputDirectory(OUTPUT_DIRECTORY + "/base");
        builderGenerator.generateBuilders();
        
        String generatedBaseBuilderFilename = "base/integrationtest/generationgap/generatedbuilder/StringPropertyBean2BaseBuilder.java";
        String expectedBaseBuilderFilename = "integrationtest/generationgap/expectedbuilder/StringPropertyBean2BaseBuilder.java";
        assertFilesEqual(expectedBaseBuilderFilename, generatedBaseBuilderFilename);
        
        String generatedBuilderFilename = "integrationtest/generationgap/generatedbuilder/StringPropertyBean2Builder.java";
        String expectedBuilderFilename = "integrationtest/generationgap/expectedbuilder/StringPropertyBean2Builder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void beanWithMapProperties() throws Exception {

        // Delete generated files, just in case the HouseBuilder is there already
        deleteGeneratedFiles();

        createBuilderGenerator(BeanWithMapProperty.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        String generatedBuilderFilename = "integrationtest/generatedbuilder/BeanWithMapPropertyBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/BeanWithMapPropertyBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);

        // TODO uncomment: a HouseBuilder should be generated (but isn't) 
        // String generatedBuilderFilename2 = "integrationtest/generatedbuilder/HouseBuilder.java";
        // String expectedBuilderFilename2 = "integrationtest/expectedbuilder/HouseBuilder.java";
        // assertFilesEqual(expectedBuilderFilename2, generatedBuilderFilename2);

        
    }

    @Test
    public void beanWithCollectionAlsoGeneratesBuilderForGenericType() throws Exception {
        
        // Delete generated files, just in case the HouseBuilder is there already
        deleteGeneratedFiles();
        
        createBuilderGenerator(Address.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY).generateBuilders();
        String generatedBuilderFilename = "integrationtest/generatedbuilder/AddressBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/AddressBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
        String generatedBuilderFilename2 = "integrationtest/generatedbuilder/HouseBuilder.java";
        String expectedBuilderFilename2 = "integrationtest/expectedbuilder/HouseBuilder.java";
        assertFilesEqual(expectedBuilderFilename2, generatedBuilderFilename2);
    }
    
    @Test
	public void configureSuperClassForBuilder() throws Exception {

        BuilderGenerator builderGenerator = createBuilderGenerator(StringPropertyBeanWithSomethingElse.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.addBuilderSuperClass(StringPropertyBeanWithSomethingElse.class, "integrationtest.generatedbuilder.Base<integrationtest.generatedbuilder.StringPropertyBeanWithSomethingElseBuilder>");
        builderGenerator.generateBuilders();
        String generatedBuilderFilename = "integrationtest/generatedbuilder/StringPropertyBeanWithSomethingElseBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/StringPropertyBeanWithSomethingElseBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
	}
    
    @Test
    public void configureSuperClassForBuilderWithGenerationGap() throws Exception {
        
        BuilderGenerator builderGenerator = createBuilderGenerator(StringPropertyBeanWithSomethingElseForGenerationGapTest.class, GENERATION_GAP_BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.setGenerationGap(true);
        builderGenerator.addBuilderSuperClass(StringPropertyBeanWithSomethingElseForGenerationGapTest.class, "integrationtest.generatedbuilder.Base<integrationtest.generatedbuilder.StringPropertyBeanWithSomethingElseForGenerationGapTest>");
        builderGenerator.generateBuilders();
        
        String generatedBaseBuilderFilename = "integrationtest/generationgap/generatedbuilder/StringPropertyBeanWithSomethingElseForGenerationGapTestBaseBuilder.java";
        String expectedBaseBuilderFilename = "integrationtest/generationgap/expectedbuilder/StringPropertyBeanWithSomethingElseForGenerationGapTestBaseBuilder.java";
        assertFilesEqual(expectedBaseBuilderFilename, generatedBaseBuilderFilename);
        
        String generatedBuilderFilename = "integrationtest/generationgap/generatedbuilder/StringPropertyBeanWithSomethingElseForGenerationGapTestBuilder.java";
        String expectedBuilderFilename = "integrationtest/generationgap/expectedbuilder/StringPropertyBeanWithSomethingElseForGenerationGapTestBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void configureSuperClassForBuilderUsingCustomerClassMethod() throws Exception {

        BuilderGenerator builderGenerator = createBuilderGenerator(Ford.class, BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.addBuilderSuperClass(Ford.class, CustomerCarBuilder.class);
        builderGenerator.generateBuilders();
        String generatedBuilderFilename = "integrationtest/generatedbuilder/FordBuilder.java";
        String expectedBuilderFilename = "integrationtest/expectedbuilder/FordBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    @Test
    public void configureSuperClassForBuilderUsingCustomerClassMethodWithGenerationGap() throws Exception {

        BuilderGenerator builderGenerator = createBuilderGenerator(Audi.class, GENERATION_GAP_BUILDER_PACKAGE, OUTPUT_DIRECTORY);
        builderGenerator.setGenerationGap(true);
        builderGenerator.addBuilderSuperClass(Audi.class, CustomerCarBuilder.class);
        builderGenerator.generateBuilders();
        
        String generatedBaseBuilderFilename = "integrationtest/generationgap/generatedbuilder/AudiBaseBuilder.java";
        String expectedBaseBuilderFilename = "integrationtest/generationgap/expectedbuilder/AudiBaseBuilder.java";
        assertFilesEqual(expectedBaseBuilderFilename, generatedBaseBuilderFilename);
        
        String generatedBuilderFilename = "integrationtest/generationgap/generatedbuilder/AudiBuilder.java";
        String expectedBuilderFilename = "integrationtest/generationgap/expectedbuilder/AudiBuilder.java";
        assertFilesEqual(expectedBuilderFilename, generatedBuilderFilename);
    }

    private static void deleteGeneratedFiles() {
        URL url = ClassLoader.getSystemResource("integrationtest/generatedbuilder");
        if (url == null) {
        	// if its null then directory doesn't exist yet in which case there is nothing to do 
        	return;
        }
        // https://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html
        File directory;
        try {
            directory = new File(url.toURI());
        } catch(URISyntaxException e) {
            directory = new File(url.getPath());
        }
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
        }
    }

}
