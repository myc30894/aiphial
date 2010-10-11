/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.enhaus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import ru.nickl.meanShift.enhaus.ProcessEnhaustioner.Value;

/**
 * Операция переборщика записывающая результаты в файлы.
 * Абстрактный класс не поределяет какие именно данные должны быть зписаны в файлы.
 * Это должен решить наследник, реализовав метод {@link #genImage(java.lang.Object, ru.nickl.meanShift.enhaus.ProcessEnhaustioner.Value[]) }
 * Класс предоставляет методы {@link #genFile(ru.nickl.meanShift.enhaus.ProcessEnhaustioner.Value[])}
 * и {@link #nameGenerator(java.lang.Iterable) } для формирования имени файла
 * в зависимости от значений параметров.
 * Позволяет распределить создаваемые файлы по папкам
 * @author nickl
 */
public abstract class WriteToFile<T> implements Operation<T> {
    private File folder;
    private int[] struct;

    /**
     * Конструктор, создающий обработчик
     * @param folder_ - корневая папка, в которую будут записаны файлы.
     * ВАЖНО: папка будет очищена при вызове
     * @param str порядок разбиения результатов на папки. числа означают
     * какое количесво параметров будет формировать следующий уровень вложенности
     * То есть например если параметрами являются 2,3 то первые два параметра
     * будут вормировать имя первого вуровня вложенности папки, средующий три - втрого,
     * а оставшиеся - имя конечного файла
     * В сумме числа не должны превышать количество параметров-1
     */
    public WriteToFile(File folder_, int... str)
    {


        this.folder = folder_;
        struct = str;

        createFolder(folder);
    }


    public void process(T imageGenerator, Value[] values)
    {
        try
        {
            BufferedImage regionsImage = genImage(imageGenerator, values);
            ImageIO.write(regionsImage, "bmp", genFile(values));
        } catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Генерирует изображение, которое должно будет быть записано в файл.
     * Параметры imageGenerator-а уже предустановлены на этапе вызова этого метода.
     * Методы связанные с генерацией изображения должны вызываться здесть.     *
     * @param imageGenerator
     * @param values
     * @return
     */
    protected abstract BufferedImage genImage(T imageGenerator, Value[] values);

    private void clearrecurcively(File fold)
    {
        if (fold.exists())
        {
            for (File file : fold.listFiles())
            {
                if(file.isDirectory()) clearrecurcively(file);
                file.delete();
            }
        }
    }

    private void createFolder(File fold)
    {
        clearrecurcively(fold);
        fold.mkdir();
    }

    /**
     * Создает путь к файлу, по принципу, сформированному дескриптрами,
     * указанными в конструкторе.
     * @param values
     * @return
     */
    protected  File genFile(Value[] values)
    {
        List<Value> queue = new ArrayList<Value>(Arrays.asList(values));

        File folder0 = folder;

        for (int i = 0; i < struct.length; i++)
        {
            if(struct[i]>queue.size()) throw new IllegalArgumentException("struct params wasn't setted properly");

            List<Value> subList = queue.subList(0, struct[i]);
            String foldername = nameGenerator(subList);
            subList.clear();

            folder0 = new File(folder0, foldername);
        }

        if(queue.size()==0) throw new IllegalArgumentException("struct params wasn't setted properly");

        folder0.mkdirs();


        return new File(folder0, nameGenerator(queue) + ".bmp");
    }


    /**
     * Утилитарная функция, создающая составное имя из имен и значений параметров
     * @param values параметры, учавствующие в формировании имени.
     * Например параметры ColorRange=3 и SquareRange=7 созадут имя ColorRange3_SquareRange7
     * @return строка, содержшая параметры
     */
    protected String nameGenerator(Iterable<Value> values)
    {
        StringBuilder sb = new StringBuilder();

        for (Value value : values)
        {
            sb.append(value.getParam().getFieldname());
            sb.append(value.getValue());
            sb.append("_");
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

}
