# PROJECT_CONTEXT

## プロジェクト概要

Spring Boot（REST API）+ React（SPA）+ Neon PostgreSQL の TODO アプリ。
学習目的で構築。CRUD 操作・テスト手法・CI/CD・ブランチ運用を実践する。

## ディレクトリ構成

- backend/ — Spring Boot 3.5.x（Gradle, Java 17）
- frontend/ — React 18 + Vite（TypeScript）
- .github/workflows/ — GitHub Actions CI

## 開発ルール

- Issue ごとにブランチを切る（feature/{番号}-{説明}）
- PR → CI GREEN → Squash merge
- 実装とテストはセットで書く
- DB 接続情報は環境変数で管理（.env + .gitignore）

## テスト方針

- バックエンド: JUnit 5 + Mockito + MockMvc（テスト時は H2）
- フロントエンド: Vitest + React Testing Library + MSW
