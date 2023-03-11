package com.example.demo.controller;

import static java.util.Optional.ofNullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class DocsTest extends AbstractControllerTest {

  @Autowired
  private ApplicationContext context;

  @Test
  void generateDocs() throws Exception {
    final var controllers = new ArrayList<ControllerInfo>();

    for (String controllerName : context.getBeanNamesForAnnotation(RestController.class)) {
      final var controllerBean = context.getBean(controllerName);
      final var baseApiPath = getApiPath(
          AnnotationUtils.findAnnotation(controllerBean.getClass(), RequestMapping.class));
      final var controllerSecurityInfo = new ControllerInfo(
          StringUtils.capitalize(controllerName),
          new ArrayList<>()
      );
      for (Method method : controllerBean.getClass().getMethods()) {
        getMethodInfo(method)
            .map(m -> m.withPrefixedApiPath(baseApiPath))
            .ifPresent(m -> controllerSecurityInfo.methods().add(m));
      }
      controllers.add(controllerSecurityInfo);
    }

    final var html = """
        <html>
        <head>
            <meta charset="UTF8">
            <style>
                body, table {
                    font-family: "JetBrains Mono";
                    font-size: 20px;
                }
                table, th, td {
                  border: 1px solid black;
                }
            </style>
            <link href='https://fonts.googleapis.com/css?family=JetBrains Mono' rel='stylesheet'>
        </head>
        <body>
            <div>
                <h2>Endpoints role checking</h2>
                <div>{docs}</div>
            </div>
        </body>
        </html>
        """.replace("{docs}", toHtml(controllers));

    writeFileToBuildFolder("index.html", html);
  }

  @With
  private record ControllerInfo(
      String name,
      List<MethodInfo> methods
  ) {

  }

  @With
  private record MethodInfo(
      String httpMethod,
      String apiPath,
      String security,
      String functionName
  ) {

    public MethodInfo withPrefixedApiPath(String prefixedApiPath) {
      return withApiPath(prefixedApiPath + this.apiPath);
    }
  }

  private void writeFileToBuildFolder(String filename, String content) throws Exception {
    final var uri = getClass().getResource("/").toURI();
    Files.createDirectories(
        Path.of(uri).resolve("security-docs")
    );
    Files.writeString(
        Path.of(uri).resolve("security-docs").resolve(filename),
        content
    );
  }

  private static Optional<MethodInfo> getMethodInfo(Method method) {
    return Optional.<Annotation>ofNullable(AnnotationUtils.findAnnotation(method, GetMapping.class))
        .or(() -> ofNullable(AnnotationUtils.findAnnotation(method, PostMapping.class)))
        .or(() -> ofNullable(AnnotationUtils.findAnnotation(method, DeleteMapping.class)))
        .or(() -> ofNullable(AnnotationUtils.findAnnotation(method, PutMapping.class)))
        .map(annotation -> AnnotationUtils.getAnnotationAttributes(method, annotation))
        .map(attributes -> new MethodInfo(
            attributes.annotationType()
                .getSimpleName()
                .replace("Mapping", "")
                .toUpperCase(),
            getApiPath(attributes.getStringArray("value")),
            ofNullable(AnnotationUtils.findAnnotation(method, PreAuthorize.class))
                .map(PreAuthorize::value)
                .orElse(""),
            method.getName()
        ));
  }

  private static String getApiPath(@Nullable RequestMapping requestMapping) {
    return ofNullable(requestMapping)
        .map(RequestMapping::value)
        .map(DocsTest::getApiPath)
        .orElse("");
  }

  private static String getApiPath(@Nullable String... array) {
    return ofNullable(array)
        .map(arr -> arr.length > 0 ? arr[0] : null)
        .orElse("");
  }

  private static String toHtml(List<ControllerInfo> controllers) {
    StringBuilder docs = new StringBuilder();
    for (ControllerInfo controller : controllers) {
      docs.append("<b>")
          .append(controller.name())
          .append("</b>")
          .append("<br>")
          .append("<table>");

      for (MethodInfo method : controller.methods()) {
        docs.append("<tr>")
            .append("<td>").append(method.httpMethod()).append("</td>")
            .append("<td>").append(method.apiPath()).append("</td>")
            .append("<td>").append(method.security()).append("</td>")
            .append("<td>").append(method.functionName()).append("</td>")
            .append("</tr>");
      }
      docs.append("</table>")
          .append("----------------------------------<br>");
    }
    return docs.toString();
  }
}
