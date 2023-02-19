package com.github.yanghyu.isid.common.sequence.util;

import com.github.yanghyu.isid.common.sequence.message.SequenceSysMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class LoopUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoopUtil.class);

    public static int loopStatus(int roll) {
        roll ++;
        if (roll > 1000) {
            throw SequenceSysMessage.SYS_SEQ_TOO_MANY_LOOP.toSysException();
        } else if (roll > 500) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                logger.error("sleep interrupted exception", e);
                throw SequenceSysMessage.SYS_SEQ_LOOP_INTERRUPTED.toSysException();
            }
        }
        return roll;
    }

}
