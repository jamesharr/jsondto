package org.grickle.rebind;

import java.io.PrintWriter;

import org.grickle.client.PickleException;
import org.grickle.client.UnpickleException;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Abstract base class for pickler generators
 * 
 */
public abstract class AbstractStaticPicklerGenerator implements StaticPicklerGenerator
{
    StaticPicklerFactory factory;
    JType type;
    GeneratorContext context;
    TreeLogger logger;

    static final String JSONOBJECT_CLS = JSONObject.class.getName();
    static final String JSONVALUE_CLS = JSONValue.class.getName();
    static final String JSONNULL_CLS = JSONNull.class.getName();
    static final String JSONARRAY_CLS = JSONArray.class.getName();
    static final String UNPICKLE_EXCEPTION_CLS = UnpickleException.class.getName();
    static final String PICKLE_EXCEPTION_CLS = PickleException.class.getName();

    private boolean isGenerated = false;

    public AbstractStaticPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType type)
    {
        this.context = context;
        this.logger = logger;
        this.factory = factory;
        this.type = type;
    }

    @Override
    final public String getPicklerClassName()
    {
        return NameMangler.getPicklerPackageName(type) + "." + NameMangler.getStaticPicklerImplName(type);
    }

    /**
     * Generate the static class for pickling and unpickling.
     * 
     * Note - this will only generate the class if it hasn't been generated yet.
     */
    @Override
    final public void generate() throws UnableToCompleteException
    {
        if ( isGenerated )
            return;

        // Need to mark as generated first in case we have a recursive type.
        isGenerated = true;
        generateJavaSourceCode(logger.branch(TreeLogger.DEBUG, "Generating pickler for " + type.getParameterizedQualifiedSourceName()));
    }

    final void generateJavaSourceCode(TreeLogger logger) throws UnableToCompleteException
    {
        sanityCheck(logger);

        SourceWriter src = startClassFile(logger, getSuperClass());
        String qsn = type.getParameterizedQualifiedSourceName();

        logger.log(TreeLogger.DEBUG, "Creating pickle method");
        src.println("public static " + JSONVALUE_CLS + " pickle(" + qsn + " obj)");
        src.println("{");
        src.indent();
        writePickleBody(logger, src);
        src.outdent();
        src.println("}");

        logger.log(TreeLogger.DEBUG, "Creating unpickle method");
        src.println("public static " + qsn + " unpickle(" + JSONVALUE_CLS + " json)");
        src.println("{");
        src.indent();
        writeUnpickleBody(logger, src);
        src.outdent();
        src.println("}");

        src.commit(logger);
    }

    /**
     * Start a new implementation class file.
     * 
     * @param logger
     * @param superClass Class to inherit from, can be null.
     * @return
     */
    final SourceWriter startClassFile(TreeLogger logger, String superClass)
    {
        String packageName = NameMangler.getPicklerPackageName(type);
        String picklerImplName = NameMangler.getStaticPicklerImplName(type);
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, picklerImplName);
        if ( superClass != null )
            composerFactory.setSuperclass(superClass);
        PrintWriter printWriter = context.tryCreate(logger, packageName, picklerImplName);
        SourceWriter src = composerFactory.createSourceWriter(context, printWriter);
        return src;
    }

    /**
     * Log a message and throw an UnableToCompleteException
     * 
     * @param logger
     * @param msg
     * @throws UnableToCompleteException
     */
    final void fail(TreeLogger logger, String msg) throws UnableToCompleteException
    {
        logger.log(TreeLogger.ERROR, msg);
        throw new UnableToCompleteException();
    }

    /**
     * Return true if 'cls' is assignable to 'assignable'
     * 
     * @param cls
     * @param assignable
     * @return
     */
    final static boolean isAssignable(GeneratorContext context, JClassType cls, Class<?> assignable)
    {
        JType assignableType = context.getTypeOracle().findType(assignable.getName());
        assert(assignableType != null);
        JClassType assignableClassType = assignableType.isInterface();
        assert(assignableClassType != null);

        if ( cls.isAssignableTo(assignableClassType))
            return true;
        return false;
    }

    /**
     * Override this to perform a sanity check on a type before starting code generation
     * 
     * @param logger
     * @throws UnableToCompleteException
     */
    void sanityCheck(TreeLogger logger) throws UnableToCompleteException
    {

    }

    /**
     * Override this to set a class to inherit from when creating the implementation source code.
     * 
     * @return
     */
    String getSuperClass()
    {
        return null;
    }

    /**
     * Override this to generate the body of the 'pickle' static method. The object is passed in as 'obj'.
     * 
     * @param logger
     * @param src
     * @throws UnableToCompleteException
     */
    abstract void writePickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException;

    /**
     * Override this to generate the body of the 'unpickle' static method. The JSONObject is passed in as 'json'.
     *
     * @param logger
     * @param src
     * @throws UnableToCompleteException
     */
    abstract void writeUnpickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException;
}
