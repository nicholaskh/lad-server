package com.lad.init;

import java.util.TimeZone;

public class UTCTimeZoneConfiguration {
	public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));        
	}
}
