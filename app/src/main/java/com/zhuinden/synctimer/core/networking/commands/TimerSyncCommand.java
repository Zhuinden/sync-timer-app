/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - The binary form of the software cannot be redistributed for commercial use under the name of a separate entity
 * when the binary form is built from using all included source files, or any of the full and complete compilation modules of the software are used as a submodule of another software.
 * - Prior written permission must be given in order to use the name of its author or other project contributors
 * for endorsing or promoting products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.zhuinden.synctimer.core.networking.commands;

public class TimerSyncCommand {
    public int currentTime;
    public boolean isCountdownActive;
    public boolean isStoppedByUser;
    public String stoppedBy;
    public boolean isPaused;
    public boolean isTimerReachedEnd;

    public TimerSyncCommand() {
    }

    public TimerSyncCommand(int currentTime, boolean isCountdownActive, boolean isStoppedByUser, String stoppedBy, boolean isPaused, boolean isTimerReachedEnd) {
        this.currentTime = currentTime;
        this.isCountdownActive = isCountdownActive;
        this.isStoppedByUser = isStoppedByUser;
        this.stoppedBy = stoppedBy;
        this.isPaused = isPaused;
        this.isTimerReachedEnd = isTimerReachedEnd;
    }
}
