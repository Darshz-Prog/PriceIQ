Use openapi.json as the authoritative API specification for my existing Spring Boot backend.

Analyze all documented endpoints, request schemas, response schemas, query parameters, authentication requirements, and security definitions.

Implement the frontend using the supplied `API_CONTRACT.md` as the authoritative backend specification.

Build a simple, responsive, professional React frontend with Vite, Tailwind CSS, React Router, and Axios, unless an existing frontend stack should be preserved.

Requirements:

- Implement only features supported by the API contract.
- Match every endpoint, HTTP method, request body, query parameter, response field, authentication requirement, and role restriction exactly.
- Centralize API calls and authentication handling.
- Use real backend data; do not fabricate successful responses or unsupported features.
- Keep the implementation beginner-friendly and avoid unnecessary libraries.
- Do not modify backend files, business logic, endpoints, database schemas, or security configuration.
- Do not ask me to upload the entire backend when the supplied contract already establishes the necessary behavior.
- If a required contract detail is missing, list the exact missing information instead of guessing.
- Test the frontend build and actual API integration where the environment permits. Report what was tested and what remains unverified.

First map the supplied contract to the required pages and components. Then implement the frontend in small, verifiable stages.
