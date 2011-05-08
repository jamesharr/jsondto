package org.grickle.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates front-end Pickler<T> implementation code.
 */
public class PicklerGenerator extends Generator
{
    private static final String JSONVALUE_CLS = "com.google.gwt.json.client.JSONValue";

    /*
     * These private variables are convenient storage to avoid always passing types around
     */
    private JClassType picklerIface;
    private GeneratorContext context;

    public PicklerGenerator()
    {
    }

    private String getImplName()
    {
        return NameMangler.getPicklerPackageName(picklerIface) + "."
        + NameMangler.getPicklerImplName(picklerIface);
    }

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
    throws UnableToCompleteException
    {
        this.context = context;
        try
        {
            logger = logger.branch(TreeLogger.INFO, "Generating implementaiton for " + typeName);
            JType type = context.getTypeOracle().getType(typeName);
            picklerIface = type.isInterface();
            if ( picklerIface == null )
                fail(logger, "Type is not an interface: " + type.getParameterizedQualifiedSourceName());
            generate(logger);
            return getImplName();
        } catch (NotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generate the implementation code for this PicklerProxy (unless it's been done already)
     * 
     * @param logger
     * @throws UnableToCompleteException
     */
    private void generate(TreeLogger logger) throws UnableToCompleteException
    {
        // Get supporting data
        JType pickledType = getPickledType(logger);
        SourceWriter src = getWriter(logger);

        // Get static-style pickler
        StaticPicklerFactory pf = StaticPicklerFactory.getInstance();
        String staticPickler = pf.getPickler(logger, context, pickledType);

        // Generate class
        src.println("public " + JSONVALUE_CLS + " pickle(" + pickledType.getParameterizedQualifiedSourceName() + " obj)");
        src.println("{");
        src.indentln("return " + staticPickler + ".pickle(obj);");
        src.println("}");

        src.println("public " + pickledType.getParameterizedQualifiedSourceName() + " unpickle(" + JSONVALUE_CLS + " json)");
        src.println("{");
        src.indentln("return " + staticPickler + ".unpickle(json);");
        src.println("}");

        src.commit(logger);
    }

    private SourceWriter getWriter(TreeLogger logger)
    {
        String packageName = NameMangler.getPicklerPackageName(picklerIface);
        String picklerImplName = NameMangler.getPicklerImplName(picklerIface);
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, picklerImplName);
        composerFactory.addImplementedInterface(picklerIface.getParameterizedQualifiedSourceName());
        PrintWriter printWriter = context.tryCreate(logger, packageName, picklerImplName);
        SourceWriter src = composerFactory.createSourceWriter(context, printWriter);
        return src;
    }

    private JType getPickledType(TreeLogger logger) throws UnableToCompleteException
    {
        logger = logger.branch(TreeLogger.TRACE, "Checking pickler interface for " +
                picklerIface.getParameterizedQualifiedSourceName());

        // Make sure we're an interface
        if (picklerIface.isInterface() == null)
            fail(logger, "Pickler '" + picklerIface.getParameterizedQualifiedSourceName() + "' needs to be an interface.");

        // Get the two methods we should have
        JMethod pickleMethod = null, unpickleMethod = null;
        for (JMethod m : picklerIface.getOverridableMethods())
        {
            logger.log(TreeLogger.DEBUG, "Found method:" + m.getReadableDeclaration());
            if ( m.getName().equals("pickle") )
                pickleMethod = m;
            else if ( m.getName().equals("unpickle") )
                unpickleMethod = m;
            else
                fail(logger, "Unknown method signature in pickler.");
        }

        // JSONValue type
        JClassType jsonValue = context.getTypeOracle().findType(JSONVALUE_CLS);
        assert(jsonValue != null);

        // check pickle method signature
        if ( pickleMethod == null )
            fail(logger, "Could not find pickler method");
        if ( pickleMethod.getReturnType() != jsonValue )
            fail(logger, "Return type on pickler method was not a JSONValue");
        if ( pickleMethod.getParameters().length != 1 )
            fail(logger, "pickle() arguments were invalid");
        JType pickleType = pickleMethod.getParameters()[0].getType();

        // check unpickle method signature.
        if ( unpickleMethod == null )
            fail(logger, "Could not find unpickle method");
        if ( unpickleMethod.getReturnType() != pickleType )
            fail(logger, "Unpickle doesn't return our pickle type.");
        if ( unpickleMethod.getParameters().length != 1 )
            fail(logger, "unpickle() argument count is invalid");
        if ( unpickleMethod.getParameters()[0].getType() != jsonValue )
            fail(logger, "unpickle doesn't take a JSONValue");

        return pickleType;
    }

    private void fail(TreeLogger logger, String msg) throws UnableToCompleteException
    {
        logger.log(TreeLogger.ERROR, msg);
        throw new UnableToCompleteException();
    }
}
