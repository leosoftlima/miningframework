package services.dataCollectors.modifiedLinesCollector

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticBlockModifiedLinesMatcher {
    private String CONST_INITIALIZER_DECLARATION = "static-";
    Set<ModifiedStaticBlock> matchModifiedStaticBlocksASTLines(Map<String, String> collectionsStaticBlocks, List<ModifiedLine> modifiedLines) {
        def staticBlockSet = new HashSet<ModifiedStaticBlock>();

        if (collectionsStaticBlocks.entrySet().size() > 0) {
            for (def initializerDeclaration : collectionsStaticBlocks.entrySet()) {
                String identifier = CONST_INITIALIZER_DECLARATION + initializerDeclaration.getKey();
                String blockedStatic = initializerDeclaration.getValue();

                def lineSet = new HashSet<ModifiedLine>();
                def diffLineText = findModifiedLineByText(identifier,blockedStatic, modifiedLines);

                if (diffLineText != null) {
                    lineSet.add(diffLineText);
                    staticBlockSet.add(new ModifiedStaticBlock(identifier, lineSet));
                }
            }
        }
        return staticBlockSet;
    }

    Set<ModifiedStaticBlock> matchModifiedStaticBlocksAndLines(Map<String, int[]> modifiedStaticBlocks, List<ModifiedLine> modifiedLines) {
        def staticBlockSet = new HashSet<ModifiedStaticBlock>();

        if(modifiedStaticBlocks.entrySet().size() > 0 ) {
            for (def staticBlock : modifiedStaticBlocks.entrySet()) {
                String identifier = staticBlock.getKey();
                int[] lines = staticBlock.getValue();

                def lineSet = new HashSet<ModifiedLine>();

                for (def lineNumber : lines) {
                    def diffLine = findModifiedLineByNumber(lineNumber, modifiedLines);

                    if (diffLine != null) {
                        lineSet.add(diffLine);
                    }
                }
                staticBlockSet.add(new ModifiedStaticBlock(identifier, lineSet));
            }
        }

        return staticBlockSet;
    }
    public ModifiedLine findModifiedLineByText(String keyBlockedStatic , String blockedStatic, List<ModifiedLine> modifiedLines) {
        for (def modifiedLine: modifiedLines) {
            if (Pattern.compile(modifiedLine.getContent(),Pattern.LITERAL).matcher(blockedStatic).find()) {
                if(findModifiedLineByNumber(keyBlockedStatic, modifiedLines))
                   return modifiedLine;
            }
        }
        return null;
    }
    public ModifiedLine findModifiedLineByNumber(int lineNumber, List<ModifiedLine> modifiedLines) {
        for (def modifiedLine: modifiedLines) {
            if (modifiedLine.getNumber() == lineNumber) {
                return modifiedLine;
            }
        }
        return null;
    }
    public int getBeginIdentifier(String keyLineNumber){
        return Integer.parseInt(keyLineNumber.split("-")[1]);
    }
    public int getEndIdentifier(String keyLineNumber){
        return Integer.parseInt(keyLineNumber.split("-")[2]);
    }
    public boolean findModifiedLineByNumber(String lineNumberBeginEnd, List<ModifiedLine> modifiedLines) {
        for (def modifiedLine: modifiedLines) {
            if (modifiedLine.getNumber() >= getBeginIdentifier(lineNumberBeginEnd) && modifiedLine.getNumber() <= getEndIdentifier(lineNumberBeginEnd) ) {
                return true;
            }
        }
        return false;
    }

}