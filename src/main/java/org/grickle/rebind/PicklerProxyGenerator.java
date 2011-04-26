package org.grickle.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates PicklerProxy
 */
public class PicklerProxyGenerator
{
    private static final String JSONVALUE_CLS = "com.google.gwt.json.client.JSONValue";

    private JClassType picklerIface;
    private boolean generated = false;
    private GeneratorContext context;

    public PicklerProxyGenerator(TreeLogger logger, GeneratorContext context, JType pickler) throws UnableToCompleteException
    {
        if ( pickler.isInterface() != null )
            this.picklerIface = pickler.isInterface();
        else
            fail(logger, "Type is not an interface: " + pickler.getQualifiedSourceName());
        this.context = context;
    }

    /**
     * Get the name of the implementation for this PicklerProxy
     * 
     * @return
     */
    public String getImplName()
    {
        return NameMangler.getPackageName(picklerIface) + "."
        + NameMangler.getProxyImplName(picklerIface);
    }

    /**
     * Generate the implementation code for this PicklerProxy (unless it's been done already)
     * @param logger
     * @throws UnableToCompleteException
     */
    public void generate(TreeLogger logger) throws UnableToCompleteException
    {
        if ( generated )
            return;

        generated = true;

        // Get supporting data
        JClassType pickledType = getPickledType(logger, context.getTypeOracle());
        SourceWriter src = getWriter(logger);

        // Get static-style pickler
        PicklerGeneratorFactory pgf = PicklerGeneratorFactory.getInstance();
        PicklerGenerator pg = pgf.getPicklerGenerator(pickledType);
        pg.generate();
        String staticPickler = pg.getPicklerClassName();

        // Generate class
        src.indent();
        src.println("public " + JSONVALUE_CLS + " pickle(" + pickledType.getQualifiedSourceName() + " obj)");
        src.println("{");
        src.indentln("return " + staticPickler + ".pickle(obj);");
        src.println("}");

        src.println("public " + pickledType.getQualifiedSourceName() + " unpickle(" + JSONVALUE_CLS + " json)");
        src.println("{");
        src.indentln("return " + staticPickler + ".unpickle(json);");
        src.println("}");

        src.commit(logger);
    }

    private SourceWriter getWriter(TreeLogger logger)
    {
        String packageName = NameMangler.getPackageName(picklerIface);
        String picklerImplName = NameMangler.getProxyImplName(picklerIface);
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, picklerImplName);
        composerFactory.addImplementedInterface(picklerIface.getQualifiedSourceName());
        PrintWriter printWriter = context.tryCreate(logger, packageName, picklerImplName);
        SourceWriter src = composerFactory.createSourceWriter(printWriter);
        return src;
    }

    private JClassType getPickledType(TreeLogger logger, TypeOracle oracle) throws UnableToCompleteException
    {
        logger = logger.branch(TreeLogger.TRACE, "Checking pickler interface for " +
                picklerIface.getQualifiedSourceName());

        // Make sure we're an interface
        if (picklerIface.isInterface() == null)
            fail(logger, "Pickler '" + picklerIface.getQualifiedSourceName() + "' needs to be an interface.");

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
        JClassType jsonValue = oracle.findType(JSONVALUE_CLS);
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

        return pickleType.isClass();
    }

    private void fail(TreeLogger logger, String msg) throws UnableToCompleteException
    {
        logger.log(TreeLogger.ERROR, msg);
        throw new UnableToCompleteException();
    }
}
