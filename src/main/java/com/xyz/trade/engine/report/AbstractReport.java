package com.xyz.trade.engine.report;

import com.xyz.trade.engine.model.Instruction;
import com.xyz.trade.engine.util.TEConstants;
import com.xyz.trade.engine.util.TradeUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This class holds the common logic for report generation.
 */
public abstract class AbstractReport implements IReport {

    /**
     * For a given list of instructions this method generates the
     * report map with settlement date as key.
     * @param instuctions
     * @return
     */
    protected Map<String, Double> generateTradeAmntReportMap(List<Instruction> instuctions) {
        Map<String, Double> reportMap = new TreeMap<>();
        for (Instruction instr  : instuctions) {
            Double tradeAmnt = reportMap.get(TradeUtil.getDateStr(instr.getSettlementDate(), TEConstants.TEDATEFORMAT));
            if (null == tradeAmnt) {
                tradeAmnt = instr.getTradeAmntInUSD();
            } else {
                tradeAmnt = tradeAmnt + instr.getTradeAmntInUSD();
            }
            reportMap.put(TradeUtil.getDateStr(instr.getSettlementDate(), TEConstants.TEDATEFORMAT), tradeAmnt);
        }
        return reportMap;
    }

    /**
     * For a given list of instructions this method generates the
     * report map for entity rankings.
     * @param instructions
     * @return
     */
    protected Map<String, Double> generateRankingsReportMap(List<Instruction> instructions) {

        List<Instruction> sortedList = instructions.stream()
                .sorted(Comparator.comparing(Instruction::getTradeAmntInUSD).reversed())
                .collect(Collectors.toList());
        Map<String, Double> sortedMap = new LinkedHashMap();
        sortedList.stream().forEach(instr -> sortedMap.putIfAbsent(instr.getEntity(), instr.getTradeAmntInUSD()));
        return sortedMap;
    }

    /**
     * Filters out all the BUY instructions from a given LIST
     * @param instructions
     * @return
     */
    protected List<Instruction> getBuyInstructions(List<Instruction> instructions) {
        return instructions.stream()
                .filter(instr ->  TEConstants.BUYSELL.B.name().equalsIgnoreCase(instr.getBuySell().name()))
                .collect(Collectors.toList());
    }

    /**
     * Filters out all the SELL instructions from a given LIST
     * @param instructions
     * @return
     */
    protected List<Instruction> getSellInstructions(List<Instruction> instructions) {
        return instructions.stream()
                .filter(instr ->  TEConstants.BUYSELL.S.name().equalsIgnoreCase(instr.getBuySell().name()))
                .collect(Collectors.toList());
    }

    /**
     * Prints the output to the console for daily settlement amount report
     * @param reportMap
     * @param io
     */
    protected void printIOReport(Map<String, Double> reportMap, String io) {
        System.out.println("\n:: ====================================================== :: ");
        System.out.println(":: Report - Amount in USD settled " + io + " everyday :: ");
        System.out.println(":: ====================================================== :: ");
        reportMap.entrySet().stream().forEach(e -> System.out.println("-- Date is "+e.getKey()+" Incoming Amount is "+e.getValue()));
        System.out.println(":: ====================================================== :: \n");
    }

    /**
     * Prints the output to the console for entity rankings report
     * @param reportMap
     * @param io
     */
    protected void printRankingsReport(Map<String, Double> reportMap, String io) {
        System.out.println("\n:: ====================================================== :: ");
        System.out.println(":: Report - Ranking of entities based on "+io+" :: ");
        System.out.println(":: ====================================================== :: ");
        AtomicInteger rank = new AtomicInteger(0);
        reportMap.entrySet().stream()
                .forEach(e -> System.out.println("-- Entity " +e.getKey()
                        +" Rank is " +rank.incrementAndGet()
                        +" Amount is "+e.getValue()));
        System.out.println(":: ====================================================== :: \n");
    }
}
