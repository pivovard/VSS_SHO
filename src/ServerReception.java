import cz.zcu.fav.kiv.jsim.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pivov on 20-Jan-19.
 */
public class ServerReception extends Server{

    public ServerReception(String name, JSimSimulation simulation, double mu, double var, Queue queue, List<Queue> output) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, simulation, mu, var, queue, output);
        probability = 0.99;
        probabilityMed = 0.33;
    }

    protected void life()
    {
        Transaction t;
        JSimLink link;

        try
        {
            while (true)
            {
                if (queue.empty())
                {
                    passivate();
                }
                else
                {
                    link = queue.first();

                    double p = JSimSystem.uniform(0.0, 1.0);
                    if (p < probability)
                    {
                        link.out();
                        t = (Transaction) link.getData();

                        counter++;
                        transTq += myParent.getCurrentTime() - t.getCreationTime();

                        p = JSimSystem.uniform(0.0, 1.0);
                        Queue q = null;
                        if(p < probabilityMed){
                            q = output.get(1);
                        }
                        else if(p < 2*probabilityMed){
                            q = output.get(2);
                        }
                        else{
                            q = output.get(3);
                        }

                        link.into(q);
                        q.trans(myParent.getCurrentTime());
                        if (q.getServer().isIdle())
                            q.getServer().activate(myParent.getCurrentTime());
                    }
                    else{
                        link.out();
                        t = (Transaction) link.getData();

                        counter++;
                        transTq += myParent.getCurrentTime() - t.getCreationTime();

                        Queue q = output.get(0);
                        link.into(q);
                        q.trans(myParent.getCurrentTime());
                        if (q.getServer().isIdle())
                            q.getServer().activate(myParent.getCurrentTime());
                    }

                    double holdTime = getHoldTime();
                    transTs += holdTime;
                    hold(holdTime);
                }
            }
        }
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }
    }
}
