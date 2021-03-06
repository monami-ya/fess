/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.job;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.JobLogService;
import org.codelibs.fess.es.config.exentity.JobLog;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.job.ScheduledJobException;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.LaJobRuntime;
import org.lastaflute.job.key.LaJobUnique;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptExecutorJob implements LaJob {
    private static final Logger logger = LoggerFactory.getLogger(ScriptExecutorJob.class);

    @Override
    public void run(final LaJobRuntime runtime) {
        final ScheduledJob scheduledJob = (ScheduledJob) runtime.getParameterMap().get(Constants.SCHEDULED_JOB); // TODO null check
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final JobManager jobManager = ComponentUtil.getJobManager();
        final JobLog jobLog = new JobLog(scheduledJob);
        final String scriptType = scheduledJob.getScriptType();
        final String script = scheduledJob.getScriptData();
        final String id = scheduledJob.getId();
        final JobExecutor jobExecutor = ComponentUtil.getJobExecutor(scriptType);
        if (jobExecutor == null) {
            throw new ScheduledJobException("No jobExecutor: " + scriptType);
        }

        if (!jobManager.findJobByUniqueOf(LaJobUnique.of(id)).isPresent()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Job " + id + " is running.");
            }
            return;
        }

        try {
            if (scheduledJob.isLoggingEnabled()) {
                storeJobLog(jobLog);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Starting Job " + id + ". scriptType: " + scriptType + ", script: " + script);
            } else if (scheduledJob.isLoggingEnabled() && logger.isInfoEnabled()) {
                logger.info("Starting Job " + id + ".");
            }

            final Object ret = jobExecutor.execute(script);
            if (ret == null) {
                if (scheduledJob.isLoggingEnabled() && logger.isInfoEnabled()) {
                    logger.info("Finished Job " + id + ".");
                }
            } else {
                if (scheduledJob.isLoggingEnabled() && logger.isInfoEnabled()) {
                    logger.info("Finished Job " + id + ". The return value is:\n" + ret);
                }
                jobLog.setScriptResult(ret.toString());
            }
            jobLog.setJobStatus(Constants.OK);
        } catch (final Throwable t) {
            logger.error("Failed to execute " + id + ": " + script, t);
            jobLog.setJobStatus(Constants.FAIL);
            jobLog.setScriptResult(systemHelper.abbreviateLongText(t.getLocalizedMessage()));
        } finally {
            jobLog.setEndTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
            if (logger.isDebugEnabled()) {
                logger.debug("jobLog: " + jobLog);
            }
            if (scheduledJob.isLoggingEnabled()) {
                storeJobLog(jobLog);
            }
        }
    }

    private void storeJobLog(final JobLog jobLog) {
        final JobLogService jobLogService = SingletonLaContainer.getComponent(JobLogService.class);
        jobLogService.store(jobLog);
    }

}
