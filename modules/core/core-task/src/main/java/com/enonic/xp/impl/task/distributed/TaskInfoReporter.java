package com.enonic.xp.impl.task.distributed;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.impl.task.TaskManager;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

public class TaskInfoReporter
    implements Callable<List<TaskInfo>>, Serializable
{
    private final TaskId taskId;

    private final boolean filterRunning;

    public TaskInfoReporter( final TaskId taskId )
    {
        this.taskId = taskId;
        this.filterRunning = true;
    }

    public TaskInfoReporter( boolean filterRunning )
    {
        this.taskId = null;
        this.filterRunning = filterRunning;
    }

    @Override
    public List<TaskInfo> call()
        throws Exception
    {
        final BundleContext bundleContext = FrameworkUtil.getBundle( TaskManager.class ).getBundleContext();
        final ServiceReference<TaskManager> serviceReference = bundleContext.getServiceReference( TaskManager.class );
        if ( serviceReference != null )
        {
            final TaskManager taskManager = bundleContext.getService( serviceReference );
            try
            {
                if ( taskManager != null )
                {
                    if ( taskId != null )
                    {
                        return Optional.ofNullable( taskManager.getTaskInfo( taskId ) ).map( List::of ).orElse( List.of() );
                    }
                    else if ( filterRunning )
                    {
                        return List.copyOf( taskManager.getRunningTasks() );
                    }
                    else
                    {
                        return List.copyOf( taskManager.getAllTasks() );
                    }
                }
                else
                {
                    return List.of();
                }
            }
            finally
            {
                bundleContext.ungetService( serviceReference );
            }
        }
        else
        {
            return List.of();
        }
    }
}
