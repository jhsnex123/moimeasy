package com.kosa.moimeasy.transaction.service;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.transaction.dto.TransferRequestDTO;
import com.kosa.moimeasy.transaction.entity.Transaction;
import com.kosa.moimeasy.transaction.repository.CategoryRepository;
import com.kosa.moimeasy.transaction.repository.TransactionRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final UserRepository userRepository;
    private final MoeimRepository moeimRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    // 이체 실행
    public void executeTransfer(TransferRequestDTO transferRequestDTO){
        Long userId = transferRequestDTO.getUserId(); // 회원 아이디
        Long moeimId = transferRequestDTO.getMoeimId(); // 모임 아이디

        User userAccount = getUserAccount(userId); // 회원 계좌
        Moeim moeimAccount = getMoeimAccount(moeimId); // 모임 계좌

        double transferAmount = transferRequestDTO.getTransferAmount(); // 이체 금액

        userAccount.withdraw(transferRequestDTO.getTransferAmount()); // 회원 계좌 출금
        moeimAccount.deposit(transferRequestDTO.getTransferAmount()); // 모임 계좌 입금

        // 거래 내역 저장
        Transaction transaction = Transaction.builder()
                .user(userAccount) // 회원 계좌
                .moeim(moeimAccount) // 모임 계좌
                .transferAmount(transferAmount) // 이체 금액
                .withdrawalAmount(userAccount.getBalance()) // 회원 계좌의 잔액 (출금 후)
                .depositAmount(moeimAccount.getBalance()) // 모임 계좌의 잔액 (입금 후)
                .content(transferRequestDTO.getContent()) // 거래 내용
                .transactionType(1) // (출금: 0 입금 : 1)
                .categoryName("회비 입금") // 소비 항목
                .build();

        // 회원 계좌에서 출금, 모임 계좌에 입금 내역 기록
        transactionRepository.save(transaction); // 거래 내역 저장
    }

    // 거래내역 조회
    public List<Transaction> getTransferHistory(){
        return transactionRepository.findAll();
    }


    // 모임 계좌 조회
    private Moeim getMoeimAccount(Long moeimId){
        return moeimRepository.findAccountNumberById(moeimId)
                .orElseThrow(IllegalArgumentException::new);
    }

    // 회원 계좌 조회
    private User getUserAccount(Long userId){
        return userRepository.findAccountNumberById(userId)
                .orElseThrow(IllegalArgumentException::new);
    }

}