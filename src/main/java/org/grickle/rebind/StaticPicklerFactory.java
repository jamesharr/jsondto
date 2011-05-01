package org.grickle.rebind;

import java.util.Map;
import java.util.TreeMap;

import org.grickle.client.IsJSONSerializable;
import org.grickle.client.PrimitivePicklers;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * Singleton used to get a static class for pickling and unpickling.
 */
public class StaticPicklerFactory
{
    private static StaticPicklerFactory instance = null;
    public static StaticPicklerFactory getInstance()
    {
        if ( instance == null )
            instance = new StaticPicklerFactory();
        return instance;
    }

    private StaticPicklerFactory()
    {
        picklerImpls.put("int", PrimitivePicklers.IntPickler.class.getName());
        picklerImpls.put("Integer", PrimitivePicklers.IntegerPickler.class.getName());
        picklerImpls.put(String.class.getName(), PrimitivePicklers.StringPickler.class.getName());
    }

    Map<String,String> picklerImpls = new TreeMap<String,String>();

    /**
     * Get a pickler for a particular type. Generate if need be.
     * 
     * @param type
     * @return
     * @throws UnableToCompleteException
     */
    public String getPickler(TreeLogger logger, GeneratorContext context, JType type) throws UnableToCompleteException
    {
        // This needs to be first to handle recursive objects
        String qsn = type.getParameterizedQualifiedSourceName();
        if ( picklerImpls.containsKey(qsn) )
            return picklerImpls.get(qsn);

        // Create a pickler
        StaticPicklerGenerator spg = null;
        if ( type.isArray() != null )
        {
            // TODO
        }
        else if ( type.isClass() != null )
        {
            JClassType classType = type.isClass();
            if ( isList(classType) )
                ; // TODO - List
            else if ( isMap(classType) )
                ;// TODO - Map
            else if ( isList(classType) )
                ; // TODO - Set
            else if ( classType.getAnnotation(IsJSONSerializable.class) != null )
                ; // TODO - Object
            else
            {
                logger.log(TreeLogger.ERROR, "Not sure how to serialize" + qsn + ". Maybe mark @IsJSONSerializable?");
                throw new UnableToCompleteException();
            }
        }
        else if ( type.isPrimitive() != null )
        {
            // Anything that's supported is added to picklerImpls statically
            logger.log(TreeLogger.ERROR, "Primitive type " + qsn + " isn't supported yet.");
            throw new UnableToCompleteException();
        }
        else
        {
            logger.log(TreeLogger.ERROR, "Not sure how to pickle " + qsn + ".");
            throw new UnableToCompleteException();
        }

        // Save mapping first, then generate code (recursive objects)
        assert(spg != null);
        String picklerImplName = spg.getPicklerClassName();
        picklerImpls.put(qsn, picklerImplName);
        spg.generate();

        // Done
        return picklerImplName;
    }

    private boolean isMap(JClassType classType)
    {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean isList(JClassType classType)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
