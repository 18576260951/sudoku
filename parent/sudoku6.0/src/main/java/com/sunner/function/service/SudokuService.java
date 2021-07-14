package com.sunner.function.service;

import com.sunner.function.pojo.Sudoku;
import com.sunner.structure.common.Result;

public interface SudokuService {

    Result readSudoku(Sudoku sudoku);

    Result crackSudoku(Sudoku sudoku) throws Exception;
}
