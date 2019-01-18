/* 
 * Copyright 2019 Samer Diab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package informationretreival;
/**
 *
 * @author Samer Diab
 */
public class Timer {
    private long startTime = 0;
    private long endTime = 0;
    public void setStartTimer()
    {
        this.startTime = System.currentTimeMillis()/1000;
    }
    public void setEndTimer()
    {
        this.endTime = System.currentTimeMillis()/1000;
    }
    public long getTimeDifference()
    {
        return this.endTime - this.startTime;
    }
}
