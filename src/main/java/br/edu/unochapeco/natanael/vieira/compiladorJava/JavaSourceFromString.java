package br.edu.unochapeco.natanael.vieira.compiladorJava;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

final class JavaSourceFromString extends SimpleJavaFileObject
{
    private final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}