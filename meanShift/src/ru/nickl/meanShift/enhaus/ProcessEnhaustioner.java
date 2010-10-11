/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.enhaus;

import java.lang.reflect.Method;
import ru.nickl.meanShift.direct.LuvImageProcessor;

/**
 * Переборщик. Перебирает всевозможные параметры
 * объекнта и выполняет над ним определенную {@link Operation операцию}
 * @author nickl
 */
public class ProcessEnhaustioner<T>
{

    private T imageProcessor;
    private FieldIterator<?>[] params;

    /**
     * Конструктор
     * @param imageProcessor - объект, параметры которого будут настраиваться
     */
    public ProcessEnhaustioner(T imageProcessor)
    {
        this.imageProcessor = imageProcessor;
    }

    /**
     *
     * @param imageProcessor - объект, параметры которого будут настраиваться
     * @param operation - операция, которая будет производиться над объектом,
     * для каждого сочетния его параметров.
     * @param params - итераторы параметров, в том порядке в котором одни будут изменяться,
     * то есть первый будут меняться редко, а последние часто.
     * То есть для каждого первого будет осуществлен проход по всем вторым,
     * для каждого второго по всем третьим итд
     */
    public ProcessEnhaustioner(T imageProcessor, Operation<T> operation, FieldIterator<?>... params)
    {
        this.imageProcessor = imageProcessor;
        this.params = params;
        this.operation = operation;
    }

    /**
     *
     * @param imageProcessor - объект, параметры которого будут настраиваться
     * @param params - итераторы параметров, в том порядке в котором одни будут изменяться,
     * то есть первый будут меняться редко, а последние часто.
     * То есть для каждого первого будет осуществлен проход по всем вторым,
     * для каждого второго по всем третьим итд
     */
    public ProcessEnhaustioner(T imageProcessor, FieldIterator<?>... params)
    {
        this.imageProcessor = imageProcessor;
        this.params = params;
    }

    private Method findMethod(String fname) throws SecurityException
    {
        Method[] methods = imageProcessor.getClass().getMethods();
        Method m = null;
        for (Method method : methods)
        {
            if (method.getName().equals("set" + fname))
            {
                m = method;
                break;
            }
        }
        return m;
    }

    /**
     * Пара значение, принимаемое итератором поля на данном этапе
     * и ссылка на итератор поля
     */
    public static class Value
    {

        private Object value;
        private FieldIterator<?> param;

        private Value(Object value, FieldIterator<?> param)
        {
            this.value = value;
            this.param = param;
        }

        /**
         *
         * @return итератор поля
         */
        public FieldIterator<?> getParam()
        {
            return param;
        }

        /**
         *
         * @return значение принимаемое итератором
         */
        public Object getValue()
        {
            return value;
        }
    }
    private Value[] values;

    private void processLevel(int i)
    {
        if (i >= getParams().length)
        {
            operation.process(imageProcessor, values);
            return;
        }

        for (Object num : getParams()[i])
        {
            setFileld(getParams()[i].getFieldname(), (Number) num);
            values[i] = new Value(num, getParams()[i]);

            processLevel(i + 1);
        }
    }
    private Operation<T> operation;

    public void process(Operation<? extends T> operation)
    {
        this.operation = (Operation<T>) operation;
        process();
    }

    public void process()
    {
        values = new Value[params.length];
        processLevel(0);
    }

    // <editor-fold defaultstate="collapsed" desc="get/set methods">
    /**
     * @return массив установленных итераторов полей
     */
    public FieldIterator<?>[] getParams()
    {
        return params;
    }

    /**
     * @param params - итераторы параметров, в том порядке в котором одни будут изменяться,
     * то есть первый будут меняться редко, а последние часто.
     * То есть для каждого первого будет осуществлен проход по всем вторым,
     * для каждого второго по всем третьим итд
     */
    public void setParams(FieldIterator<?>... params)
    {
        this.params = params;
    }

    // <editor-fold defaultstate="collapsed" desc="additional methods">
    private void setFileld(String fname, Number num) throws RuntimeException
    {

        try
        {
            Method m = findMethod(fname);

            if(m==null) throw new IllegalArgumentException("no capable method to set "+fname+" is avalible");

            Class<?>[] pt = m.getParameterTypes();



            Object arg = null;
            arg = toPrimitive(pt[0], num);

            if (arg == null)
            {
                throw new IllegalArgumentException();
            }

            m.invoke(imageProcessor, arg);
        } catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private Object toPrimitive(Class<?> type, Number num)
    {
        Object arg = null;

        if (type.equals(float.class))
        {
            arg = (num).floatValue();
        }
        if (type.equals(int.class))
        {
            arg = (num).intValue();
        }
        if (type.equals(short.class))
        {
            arg = (num).shortValue();
        }
        if (type.equals(double.class))
        {
            arg = (num).doubleValue();
        }
        if (type.equals(long.class))
        {
            arg = (num).longValue();
        }
        if (type.equals(byte.class))
        {
            arg = (num).byteValue();
        }
        return arg;
    }
    //</editor-fold>
}
