package com.maple.game.osee.util;

@FunctionalInterface
public interface Func2<P1, P2, R> {

    R call(P1 p1, P2 p2);

}
