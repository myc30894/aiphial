package ru.nickl.meanShift.enhaus;
/**
 * Операция производмая {@link ProcessEnhaustioner переборщиком} над перебираемыми элементами
 * @author nickl
 * @param <T> - тип перебираемого элемента
 */
public interface Operation<T>
{

    /**
     * Выполнят обработку
     * @param t - объект настройки которого перебираются
     * @param values - массив текущих установленных параметров
     */
    void process(T t, ProcessEnhaustioner.Value[] values);
}
