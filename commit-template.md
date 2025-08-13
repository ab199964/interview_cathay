# 設定規範git commit 訊息模板(IDEA )
Vs code 可以利用版控進行控管(.github/copilot-instructions.md),但IEDA不具備此功能 
所以只能使用於個人的IEDA中

## IEDA步驟如下:
1. 在 IEDA 中打開設定檔（Settings）。
2. 下載 Copilot 插件。(有就跳過)
3.  在設定中搜尋 GitHub Copilot。
4. 在 GitHub Copilot 的設定中，找到 Custom Instructions 選項。
5. 選擇下方的 Git Commit Instructions。 (根據需求選擇workspace 或 global) 
6. 把下方內容全部貼到 Git Commit Instructions 開啟的markdown中並儲存設定。
7. 重新啟動 IEDA。

---  
**備註：**  
Copilot 的 Generate Commit Message 會根據你目前的修改內容自動生成訊息，需點選頭像按鈕啟動。
---


# ✅ Conventional Commits 規範簡介（給 Copilot 和開發者用）
Conventional Commits 是一種輕量級的提交訊息規範，旨在提供一套簡單的規則來創建明確的提交歷史，並使自動化工具更容易使用。這個規範與 SemVer（語義化版本控制）相結合，通過描述功能、修復和破壞性變更來幫助開發者理解提交的意圖。

## ✅ 常用 type 類型說明

| 類型 | 說明 |
|------|------|
| feat | ✨ 新功能（SemVer：MINOR） |
| fix  | 🐛 錯誤修正（SemVer：PATCH） |
| docs | 📚 文件更新 |
| style | 💅 程式碼格式（不影響邏輯） |
| refactor | 🔧 重構程式碼（無行為變更） |
| test | 🧪 測試相關變更 |
| chore | 🔧 工具或建構設定調整 |
| ci | 🤖 持續整合流程設定 |
| build | 📦 建構相關變更 |
| perf | ⚡ 效能優化 |
| BREAKING CHANGE | 🚨 破壞性變更（SemVer：MAJOR） |

---

## Summary
The Conventional Commits specification is a lightweight convention on top of commit messages. It provides an easy set of rules for creating an explicit commit history; which makes it easier to write automated tools on top of. This convention dovetails with SemVer, by describing the features, fixes, and breaking changes made in commit messages.

The commit message should be structured as follows:

<type>[optional scope]: <description>

[optional body]

[optional footer(s)]

The commit contains the following structural elements, to communicate intent to the consumers of your library:

- fix: a commit of the type fix patches a bug in your codebase (this correlates with PATCH in Semantic Versioning).
- feat: a commit of the type feat introduces a new feature to the codebase (this correlates with MINOR in Semantic Versioning).
- BREAKING CHANGE: a commit that has a footer BREAKING CHANGE:, or appends a ! after the type/scope, introduces a breaking API change (correlating with MAJOR in Semantic Versioning). A BREAKING CHANGE can be part of commits of any type.
- types other than fix: and feat: are allowed, for example @commitlint/config-conventional (based on the Angular convention) recommends build:, chore:, ci:, docs:, style:, refactor:, perf:, test:, and others.
- footers other than BREAKING CHANGE: <description> may be provided and follow a convention similar to git trailer format.

Additional types are not mandated by the Conventional Commits specification, and have no implicit effect in Semantic Versioning (unless they include a BREAKING CHANGE). A scope may be provided to a commit’s type, to provide additional contextual information and is contained within parenthesis, e.g., feat(parser): add ability to parse arrays.

## Examples

### Commit message with description and breaking change footer
feat: allow provided config object to extend other configs
BREAKING CHANGE: `extends` key in config file is now used for extending other config files

### Commit message with ! to draw attention to breaking change
feat!: send an email to the customer when a product is shipped

### Commit message with scope and ! to draw attention to breaking change
feat(api)!: send an email to the customer when a product is shipped

### Commit message with both ! and BREAKING CHANGE footer
chore!: drop support for Node 6
BREAKING CHANGE: use JavaScript features not available in Node 6.

### Commit message with no body
docs: correct spelling of CHANGELOG

### Commit message with scope
feat(lang): add Polish language

### Commit message with multi-paragraph body and multiple footers
fix: prevent racing of requests

Introduce a request id and a reference to latest request. Dismiss
incoming responses other than from latest request.

Remove timeouts which were used to mitigate the racing issue but are
obsolete now.

Reviewed-by: Z
Refs: #123

## Specification

The key words “MUST”, “MUST NOT”, “REQUIRED”, “SHALL”, “SHALL NOT”, “SHOULD”, “SHOULD NOT”, “RECOMMENDED”, “MAY”, and “OPTIONAL” in this document are to be interpreted as described in RFC 2119.

- Commits MUST be prefixed with a type, which consists of a noun, feat, fix, etc., followed by the OPTIONAL scope, OPTIONAL !, and REQUIRED terminal colon and space.
- The type feat MUST be used when a commit adds a new feature to your application or library.
- The type fix MUST be used when a commit represents a bug fix for your application.
- A scope MAY be provided after a type. A scope MUST consist of a noun describing a section of the codebase surrounded by parenthesis, e.g., fix(parser):
- A description MUST immediately follow the colon and space after the type/scope prefix. The description is a short summary of the code changes, e.g., fix: array parsing issue when multiple spaces were contained in string.
- A longer commit body MAY be provided after the short description, providing additional contextual information about the code changes. The body MUST begin one blank line after the description.
- A commit body is free-form and MAY consist of any number of newline separated paragraphs.
- One or more footers MAY be provided one blank line after the body. Each footer MUST consist of a word token, followed by either a :<space> or <space># separator, followed by a string value (this is inspired by the git trailer convention).
- A footer’s token MUST use - in place of whitespace characters, e.g., Acked-by (this helps differentiate the footer section from a multi-paragraph body). An exception is made for BREAKING CHANGE, which MAY also be used as a token.
- A footer’s value MAY contain spaces and newlines, and parsing MUST terminate when the next valid footer token/separator pair is observed.
- Breaking changes MUST be indicated in the type/scope prefix of a commit, or as an entry in the footer.
- If included as a footer, a breaking change MUST consist of the uppercase text BREAKING CHANGE, followed by a colon, space, and description, e.g., BREAKING CHANGE: environment variables now take precedence over config files.
- If included in the type/scope prefix, breaking changes MUST be indicated by a ! immediately before the :. If ! is used, BREAKING CHANGE: MAY be omitted from the footer section, and the commit description SHALL be used to describe the breaking change.
- Types other than feat and fix MAY be used in your commit messages, e.g., docs: update ref docs.
- The units of information that make up Conventional Commits MUST NOT be treated as case sensitive by implementors, with the exception of BREAKING CHANGE which MUST be uppercase.
- BREAKING-CHANGE MUST be synonymous with BREAKING CHANGE, when used as a token in a footer.
- "description" MUST contain both Chinese and English. The format should be: Chinese first, then English in parentheses.

---

