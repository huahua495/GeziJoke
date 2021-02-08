package com.example.libnavcompiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.libnavannotation.ActivityDestination;
import com.example.libnavannotation.FragmentDestination;
import com.google.auto.service.AutoService;

import org.checkerframework.checker.i18nformatter.qual.I18nUnknownFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "com.example.libnavannotation.FragmentDestination",
        "com.example.libnavannotation.ActivityDestination",
})
public class NavProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;


    private static final String OUTPUT_FILE_NAME = "destination.json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();

        messager.printMessage(Diagnostic.Kind.NOTE,
                "NavProcessor  init" );

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        messager.printMessage(Diagnostic.Kind.NOTE,
                "NavProcessor  process start" );
        //fargment list
        Set<? extends Element> fragmentElements = roundEnv.getElementsAnnotatedWith(FragmentDestination.class);
        //activity list
        Set<? extends Element> activityElements = roundEnv.getElementsAnnotatedWith(ActivityDestination.class);

        if (!fragmentElements.isEmpty() || !activityElements.isEmpty()) {

            HashMap<String, JSONObject> destMap = new HashMap<>();
            handleDestination(fragmentElements, FragmentDestination.class, destMap);
            handleDestination(activityElements, ActivityDestination.class, destMap);

            //create file  in app/src/main/assets

            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            try {
                FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
                String resourcePath = resource.toUri().getPath();
                messager.printMessage(Diagnostic.Kind.NOTE,
                        "create configure file destination.json path  " + resourcePath);

                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                String assetsPath = appPath + "src/main/assets/";

                File file = new File(assetsPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File outputFile = new File(file, OUTPUT_FILE_NAME);

                if (outputFile.exists()) {
                    outputFile.delete();
                }
                outputFile.createNewFile();

                //create json string
                String content = JSON.toJSONString(destMap);

                fos = new FileOutputStream(outputFile);

                writer = new OutputStreamWriter(fos);

                writer.write(content);

                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (null!=writer){
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null!=fos){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }


        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation>
            destinationClass,
                                   HashMap<String, JSONObject> destMap) {

        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;

            String pageUrl = null;
            String clazName = typeElement.getQualifiedName().toString();
            int id = Math.abs(clazName.hashCode());

            boolean needLogin = false;
            boolean asStarter = false;

            boolean isFragment = false;

            //获取注解类型
            Annotation annotation = typeElement.getAnnotation(destinationClass);

            if (annotation instanceof FragmentDestination) {
                FragmentDestination dest = (FragmentDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragment = true;

            } else if (annotation instanceof ActivityDestination) {

                ActivityDestination dest = (ActivityDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();

                isFragment = false;
            }

            if (destMap.containsKey(pageUrl)) {

                messager.printMessage(Diagnostic.Kind.ERROR,
                        "diffenter page not used same pageUrl");
            } else {
                JSONObject object = new JSONObject();
                object.put("id", id);
                object.put("needLogin", needLogin);
                object.put("asStarter", asStarter);
                object.put("pageurl", pageUrl);
                object.put("clazName", clazName);
                object.put("isFragment", isFragment);
                destMap.put(pageUrl, object);
            }

        }
    }
}
