/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.enhaus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.segmentator.Region;
import ru.nickl.meanShift.direct.segmentator.Segmentator;
import ru.nickl.meanShift.enhaus.ProcessEnhaustioner.Value;

/**
 * Операция переборщика записывающая коннуты выделенные в процессе сегментации в файлы
 * Позволяет распределить создаваемые файлы по папкам
 * @author nickl
 */
public class WriteCountuorToFile extends WriteToFile<Segmentator>
{
 
    /**
     * Констрруктор, создающий обработчик
     * @param folder_ - корневая папка, в которую будут записаны файлы.
     * ВАЖНО: папка будет очищена при вызове
     * @param str порядок разбиения результатов на папки. числа означают
     * какое количесво параметров будет формировать следующий уровень вложенности
     * То есть например если параметрами являются 2,3 то первые два параметра
     * будут вормировать имя первого вуровня вложенности папки, средующий три - втрого,
     * а оставшиеся - имя конечного файла
     * В сумме числа не должны превышать количество параметров-1 
     */
    public WriteCountuorToFile(File folder_, int... str)
    {
        super(folder_, str);
    }
    



    protected  BufferedImage genImage(Segmentator segmentator, Value[] values)
    {
        long begining = System.currentTimeMillis();
        segmentator.process();
        BufferedImage filtredImage = segmentator.getProcessedImage();
        BufferedImage regionsImage = filtredImage; //new BufferedImage(filtredImage.getWidth(), filtredImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (Region region : segmentator.getRegions())
        {
            for (Point point : region.getCountour())
            {
                regionsImage.setRGB(point.x, point.y, 0xffffff);
            }
        }
        long now = System.currentTimeMillis();
        System.out.println(now - begining + " " + nameGenerator(Arrays.asList(values)));
        return regionsImage;
    }

    
}
