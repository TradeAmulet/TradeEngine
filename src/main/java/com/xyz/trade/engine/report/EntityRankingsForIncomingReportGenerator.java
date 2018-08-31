package com.xyz.trade.engine.report;

import com.xyz.trade.engine.model.Instruction;
import com.xyz.trade.engine.util.TEConstants;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityRankingsForIncomingReportGenerator extends AbstractReportGenerator {

    @Override
    public void generate(List<Instruction> instructions) {

        List<Instruction> buyInstuctions = instructions.stream()
                .filter(instr ->  TEConstants.BUYSELL.B.name().equalsIgnoreCase(instr.getBuySell().name()))
                .collect(Collectors.toList());

        Map<String, Double> reportMap = generateRankingsReportMap(buyInstuctions);
        System.out.println(":: Ranking of entities based on Incoming Amount :: ");
        System.out.println(reportMap);

    }
}
