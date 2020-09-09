package com.github.zmilad97.miner.Service;

import com.github.zmilad97.miner.Module.Block;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockService {
    private List<Block> chain ;


//    public Transaction findTransactionByTransactionHash(Transaction transaction){
//        for(int i  = chain.size()-1 ; i >=0 ; i++ ){
//           Transaction trx =findRelatedTransaction(
//                   chain.get(i).getTransactions(),transaction.getTransactionInput().getPreviousTransactionHash());
//           if(trx != null)
//               return trx;
//    }
//        return null;
//    }

//    public Transaction findRelatedTransaction(List<Transaction> transactionList,String transactionHash) {
//        for (Transaction trx : transactionList)
//            if (trx.getTransactionHash().equals(transactionHash))
//                return trx;
//
//                return null;
//    }

    public List<Block> getChain() {
        return chain;
    }

    public void setChain(List<Block> chain) {
        this.chain = chain;
    }
}
