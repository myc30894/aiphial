/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;

/**
 *
 * @author nickl
 */
public class MultiTreadSimpleFilter extends SimpleMSFilter
{

    private ExecutorCompletionService<Void> pool = null;
            
    private int tasksCount = 0;

    @Override
    public LuvData filter(LuvData rawData)
    {

        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(5);

        pool = new ExecutorCompletionService<Void>(newFixedThreadPool);

        LuvData result = super.filter(rawData);

        for (int i = 0; i < tasksCount; i++)
        {

            try
            {
                pool.take().get();
            } catch (ExecutionException ex)
            {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex)
            {
                throw new RuntimeException(ex);
            }
        }

        newFixedThreadPool.shutdown();
        

        System.out.println("jjj");

        return result;

    }

    @Override
    protected void moveOnePoint(short x, short y)
    {
        tasksCount++;
        pool.submit(new MovePoint(x, y), null);
    }

    protected void OneThredMoveOnePoint(short x, short y)
    {
        super.moveOnePoint(x, y);
    }

    class MovePoint implements Runnable
    {

        short x;
        short y;

        public MovePoint(short x, short y)
        {
            this.x = x;
            this.y = y;
        }

        public void run()
        {
            OneThredMoveOnePoint(x, y);
        }
    }
}
