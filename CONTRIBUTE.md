# Contributing to WyvenLaunchPads

Thank you for your interest in contributing to WyvenLaunchPads! Here's how you can help.

## Development Setup

1. Fork the repository
2. Clone your fork: `git clone https://github.com/your-username/WyvenLaunchPads.git`
3. Create a branch: `git checkout -b feature/your-feature`
4. Set up development environment:
    - Java 17 or higher
    - Maven
    - IDE (preferably IntelliJ IDEA)
    - Paper/Spigot test server

## Building

1. Navigate to project directory
2. Run: `mvn clean package`
3. Find the compiled JAR in `target/` directory

## Code Style

- Follow Java naming conventions
- Use 4 spaces for indentation
- Add comments for complex logic
- Include JavaDoc for public methods
- Keep methods focused and concise

## Testing

1. Place the compiled JAR in your test server's `plugins` folder
2. Test all features:
    - Command functionality
    - Launchpad creation and linking
    - Player launching mechanics
    - Data persistence

## Submitting Changes

1. Commit your changes: `git commit -m "Description of changes"`
2. Push to your fork: `git push origin feature/your-feature`
3. Open a Pull Request with:
    - Clear description of changes
    - Reference to related issues
    - Screenshots/videos if applicable

## Bug Reports

When reporting bugs, include:

- Minecraft version
- Server software and version
- Plugin version
- Steps to reproduce
- Expected vs actual behavior

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.