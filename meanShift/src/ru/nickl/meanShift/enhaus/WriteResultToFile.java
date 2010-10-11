/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.enhaus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import ru.nickl.meanShift.direct.LuvImageProcessor;
import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.segmentator.Region;
import ru.nickl.meanShift.enhaus.ProcessEnhaustioner.Value;

/**
 *
 * @author nickl
 */
public class WriteResultToFile extends WriteToFile<LuvImageProcessor>{

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
    public WriteResultToFile(File folder_, int... str)
    {
        super(folder_, str);
    }



    @Override
    protected BufferedImage genImage(LuvImageProcessor segmentator, Value[] values)
    {
        long begining = System.currentTimeMillis();
        segmentator.process();
        BufferedImage filtredImage = segmentator.getProcessedImage();

        long now = System.currentTimeMillis();
        System.out.println(now - begining + " " + nameGenerator(Arrays.asList(values)));
        return filtredImage;
    }

}
