package com.nervii.hallowatch;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MoonPhase {
        public static int GetPhaseDay(GregorianCalendar now) {
            int lp=2551443;
            GregorianCalendar new_moon = new GregorianCalendar(1970,0,7,20,35,0);
            double phase=((now.getTimeInMillis()-new_moon.getTimeInMillis())/1000) % lp;
            int phaseDay = (int)Math.floor(phase/(24*3600))+1;
            return phaseDay;
        }
}