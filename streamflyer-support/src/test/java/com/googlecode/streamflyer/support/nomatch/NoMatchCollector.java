package com.googlecode.streamflyer.support.nomatch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import com.googlecode.streamflyer.core.AfterModification;
import com.googlecode.streamflyer.core.Modifier;

/**
 * Collects the information about noMatches during the stream processing to test them in unit tests.
 * 
 * @author rwoo
 * 
 */
public class NoMatchCollector extends NoMatch {

    private List<String> noMatchInfos = new ArrayList<String>();

    @Override
    public MatchResult processNoMatch(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            MatchResult matchResult) {

        String noMatch = characterBuffer.substring(getStartPosition(), matchResult.start());

        noMatchInfos.add(noMatch + "[MATCH]");

        return super.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, matchResult);
    }

    @Override
    public AfterModification processNoMatch(StringBuilder characterBuffer, int firstModifiableCharacterInBuffer,
            boolean endOfStreamHit, AfterModification mod, Modifier modifier) {

        String noMatch = characterBuffer.substring(getStartPosition(),
                firstModifiableCharacterInBuffer + mod.getNumberOfCharactersToSkip());

        noMatchInfos.add(noMatch + "[FETCH]");

        return super.processNoMatch(characterBuffer, firstModifiableCharacterInBuffer, endOfStreamHit, mod, modifier);
    }

    /**
     * @return Returns {@link #getNoMatchInfos()}.
     */
    public List<String> getNoMatchInfos() {
        return noMatchInfos;
    }

}