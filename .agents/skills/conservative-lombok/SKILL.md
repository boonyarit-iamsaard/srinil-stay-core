---
name: conservative-lombok
description: Use when reviewing, adding, or removing Lombok in this repository's Java/Spring code. Favor using @Getter, @Setter, and @RequiredArgsConstructor only when they remove repetitive accessor or constructor boilerplate and still leave method purpose, constructor requirements, and expected object behavior obvious to a Java/Spring reader. Keep records as records, and avoid broad Lombok usage on behavior-heavy or JPA-sensitive types.
---

# Conservative Lombok

Apply Lombok only when it removes repeated accessor methods or constructor boilerplate for fields without additional logic, and only when the generated members still match the class's intended API and lifecycle.

## Priority order

Apply these priorities from top to bottom:

1. Prefer explicit code by default.
2. Use targeted Lombok only when the class is mainly repetitive accessor or constructor code with no validation, branching, persistence rules, or security-sensitive behavior.
3. Prefer explicit code whenever Lombok makes the API harder to understand for a developer unfamiliar with the type, or when framework requirements such as Spring injection, configuration binding, or JPA lifecycle mapping would be less obvious.

## Avoid

**General:**

- `@Data`
- `@Builder` for small internal DTOs or configuration classes
- Lombok on classes where constructors or accessors carry important meaning

**Spring / JPA:**

- `@Value` on Spring or JPA types unless immutability is already explicit and safe
- Lombok-generated `equals`/`hashCode` on entities

## Repository guidance

- Entities already use targeted Lombok such as `@Getter` and protected no-args constructors. Preserve that style.
- `@ConfigurationProperties` classes may use Lombok for accessor boilerplate, but keep defaults, nested structure, and validation annotations obvious.
- Service classes may use `@RequiredArgsConstructor` when dependencies are plain `final` fields and the constructor has no logic.
- If one field is locally initialized, keep that initialization explicit and let Lombok generate the constructor for the remaining required dependencies.

## Common conflicts

- If a class already has a manual getter or setter with logic, keep the manual method and do not replace it with Lombok.
- If Lombok would generate a constructor, getter, or setter that clashes with framework annotations or custom method signatures, keep the affected members explicit.
- If generated code changes persistence behavior, validation flow, or lifecycle expectations, remove Lombok and document the reason in the review or code comment when needed.
- If Lombok is only borderline acceptable and makes the type slightly harder to scan, keep explicit code unless the trade-off is documented in the review.

## Decision check

Before adding Lombok, verify all of the following:

1. The generated code follows normal Java/Spring conventions for constructors, getters, and setters.
2. The annotation does not conflict with existing annotations or framework-specific requirements.
3. The annotation does not hide domain rules, lifecycle constraints, or persistence semantics.
4. The class becomes shorter without becoming less legible.
5. Tests still cover critical business logic, validation, persistence lifecycle behavior, and security-sensitive paths after the refactor.
6. If Lombok generates code that is unexpected or incorrect, revert to explicit code and document the reason in the review. Add a code comment only when future maintainers would otherwise miss the constraint.

## Typical choices

- Spring component/service with only `final` dependencies: `@RequiredArgsConstructor`
- Mutable bindable configuration holder: `@Getter` plus targeted `@Setter`
- Immutable data carrier already expressed as a record: leave it alone
- JPA entity: keep explicit shape, use only narrowly-scoped Lombok already established in the repo

## When asked for the "right level"

Prefer clarity over brevity. Use the minimal number of Lombok annotations required to remove repetitive boilerplate only when the resulting type remains obvious to a Java/Spring reader. If a reviewer needs to mentally expand generated code to understand the type, the change is too aggressive.
