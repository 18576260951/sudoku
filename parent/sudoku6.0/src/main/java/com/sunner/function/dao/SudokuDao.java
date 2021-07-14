package com.sunner.function.dao;

import com.sunner.function.pojo.Sudoku;

import java.util.List;

public interface SudokuDao {

    int addSudoku(Sudoku sudoku);

    List<Sudoku> findBySudoku(Sudoku sudoku);

    Sudoku findBySudokuNo(Sudoku sudoku);
}
