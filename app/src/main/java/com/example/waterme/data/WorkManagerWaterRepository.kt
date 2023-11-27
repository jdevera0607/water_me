/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.waterme.data

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.model.Plant
import com.example.waterme.worker.WaterReminderWorker
import java.util.concurrent.TimeUnit

class WorkManagerWaterRepository(context: Context) : WaterRepository {
    private val workManager = WorkManager.getInstance(context)                                      //schedules the workRequest and makes it run. getInstance(context) - Retrieves the default singleton instance of WorkManager. A Context for on-demand initialization

    override val plants: List<Plant>                                                                //override list called on from Plant.kt in the model file
        get() = DataSource.plants                                                                   //get the plant list from DataSource.kt file

    override fun scheduleReminder(duration: Long, unit: TimeUnit, plantName: String) {              //function called from WaterRepository.kt from data folder
        val data = Data.Builder()                                                                   //provides data to the Worker
        data.putString(WaterReminderWorker.nameKey, plantName)                                      //pulls the String of the plant names from the WaterReminderWorker.kt file

        val workRequestBuilder = OneTimeWorkRequestBuilder<WaterReminderWorker>()                   //define if the worker needs to be run once or periodically
            .setInitialDelay(duration, unit)                                                        //Sets an initial delay for the WorkRequest. The units of time for duration.
            .setInputData(data.build())                                                             //Adds input Data to the work
            .build()                                                                                //Builds a WorkRequest based on this Builder

        workManager.enqueueUniqueWork(                                                              //This method allows you to enqueue work requests to a uniquely-named WorkContinuation, where only one continuation of a particular name can be active at a time
            plantName + duration,                                                                   //the plant name and duration options are added to the queue
            ExistingWorkPolicy.REPLACE,                                                             //conflict resolution policy will replace existing pending (uncompleted) work with the same unique name
            workRequestBuilder                                                                      //replace item from workRequestBuilder value.
        )
    }
}