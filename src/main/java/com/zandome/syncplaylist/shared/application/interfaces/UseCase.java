package com.zandome.syncplaylist.shared.application.interfaces;

public interface UseCase<I, O> {

    O execute(I input);
}
