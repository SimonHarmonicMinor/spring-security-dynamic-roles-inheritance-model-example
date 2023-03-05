package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.ofNullable;

class DocsTest extends AbstractControllerTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void generateDocs() throws Exception {
        StringBuilder docs = new StringBuilder(300);
        for (String controllerName : applicationContext.getBeanNamesForAnnotation(RestController.class)) {
            final var controllerBean = applicationContext.getBean(controllerName);
            final var baseApiPath = getApiPath(AnnotationUtils.findAnnotation(controllerBean.getClass(), RequestMapping.class));
            docs.append("<b>").append(controllerName).append("</b><br><table>");
            for (Method controllerMethod : controllerBean.getClass().getMethods()) {
                final var methodInfoOpt = getApiPathFromControllerMethod(controllerMethod);
                if (methodInfoOpt.isEmpty()) {
                    continue;
                }
                final var methodInfo = methodInfoOpt.orElseThrow();
                docs.append("<tr><td>").append(methodInfo.method())
                    .append("</td><td>")
                    .append(baseApiPath).append(methodInfo.path())
                    .append("</td><td>")
                    .append(
                        ofNullable(AnnotationUtils.findAnnotation(controllerMethod, PreAuthorize.class))
                            .map(PreAuthorize::value)
                            .orElse("No authorization required")
                    )
                    .append("</td><td>").append(controllerMethod.getName())
                    .append("</td></tr>");
            }
            docs.append("</table>----------------------------------<br>");
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
            """.replace("{docs}", docs.toString());

        writeFileToBuildFolder("security-endpoint-docs.html", html);
    }

    private void writeFileToBuildFolder(String filename, String content) throws Exception {
        final var uri = getClass().getResource("/").toURI();
        final var pathString = Path.of(uri).toString();
        Files.writeString(
            Path.of(pathString, filename),
            content
        );
    }

    private static Optional<ControllerMethodInfo> getApiPathFromControllerMethod(Method controllerMethod) {
        return ofNullable(AnnotationUtils.findAnnotation(controllerMethod, GetMapping.class))
                   .map(a -> new ControllerMethodInfo("GET", getApiPath(a.value())))
                   .or(() ->
                           ofNullable(AnnotationUtils.findAnnotation(controllerMethod, PostMapping.class))
                               .map(a -> new ControllerMethodInfo("POST", getApiPath(a.value())))
                   )
                   .or(
                       () ->
                           ofNullable(AnnotationUtils.findAnnotation(controllerMethod, DeleteMapping.class))
                               .map(a -> new ControllerMethodInfo("DELETE", getApiPath(a.value())))
                   )
                   .or(
                       () ->
                           ofNullable(AnnotationUtils.findAnnotation(controllerMethod, PutMapping.class))
                               .map(a -> new ControllerMethodInfo("PUT", getApiPath(a.value())))
                   );
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
                   .orElse(" ");
    }

    private record ControllerMethodInfo(String method, String path) {
    }
}
