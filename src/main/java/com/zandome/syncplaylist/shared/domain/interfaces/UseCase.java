package com.zandome.syncplaylist.shared.domain.interfaces;

public interface UseCase<I, O> {

    O execute(I input);
}
