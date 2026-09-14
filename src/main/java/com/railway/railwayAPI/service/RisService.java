package com.railway.railwayAPI.service;

import java.util.Map;

/**
 * Rail information services (RIS): PNR status, live running status, train
 * schedule and coach position, backed by the redBus {@code loco.redbus.com}
 * host via {@link com.railway.railwayAPI.facade.RisFacade}.
 */
public interface RisService {

    Map<String, Object> getPnrStatus(String pnr, String mobile);

    Map<String, Object> getTrainSchedule(String trainNo);

    Map<String, Object> getLiveTrainStatus(String trainNo);

    Map<String, Object> getCoachPosition(String trainNo, String stn);
}
