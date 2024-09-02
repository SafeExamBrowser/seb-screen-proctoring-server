SEB Screen Proctoring Service
======

About 
-----------------------------------

SEB Server Screen Proctoring Webservice (sps-webservice) is a subproject of the `SEB Server <https://github.com/SafeExamBrowser/seb-server>`_ project and
a new webservice component running beside the seb-server service to provide screen proctoring features
for SEB Server.

Together with the SEB Server Screen Proctoring Guiservice (sps-guiservice), this service, that fully integrates to SEB Server, allows to configure and apply screen proctoring for exams with the `Safe Exam Browser (SEB) <https://safeexambrowser.org>`_.

The Image below shows the two screen proctoring components, sps-webservice and sps-guiservice, together with SEB Server and SEB Client and how they interact with each-other. This shows also how a fully bundled of SEB Server setup since version 2.0, would look like.

.. image:: https://raw.githubusercontent.com/SafeExamBrowser/seb-screen-proctoring-server/tree/dev-1.0/docu/Screen_Proctoring_Architecture.png
    :target: https://github.com/SafeExamBrowser/seb-screen-proctoring-server/tree/dev-1.0/docu/Screen_Proctoring_Architecture.png
    

SEB - SEB Server Compatibility
------------------------------

The table below shows available and upcoming SEB client versions that has SEB Server integration support and are compatible with particular
SEB Server versions. There is an entry for each platform with a beta or testing release date and an official release date.

**SPS webservice version 1.0 is fully compatible with SEB Server version 2.0**

SEB Client Compatibility:

.. csv-table::
    :header: "Platform / OS", "Release Version"
    
    "SEB Client for iOS", "3.4"
    "SEB Client for Mac", "3.4"
    "SEB Client for Windows", "3.8"

Install SEB Server
------------------

For a complete guide to install SEB Server please go to `SEB Server Installation Guide <https://seb-server-setup.readthedocs.io/en/latest/overview.html>`_

Getting started with SEB Server
-------------------------------

For a complete SEB Server user guide please go to `SEB Server User Guide <https://seb-server.readthedocs.io/en/latest/#>`_


Contributing to SEB Server
---------------------------

We want to make contributing to this project as easy and transparent as possible, whether it's:

- Give us a star
- Reporting a bug
- Submitting a fix
- Proposing new features
- Becoming a SEB Alliance member

We use github to host code, to track issues and feature requests, as well as accept pull requests.
And we use `Github issues <https://github.com/SafeExamBrowser/seb-server/issues>`_ to track public bugs.
Report a bug by [opening a new issue]();

**Before enter a new bug-report, ensure the bug was not already reported**

Please fill and provide all the information suggested by the bug-report template
Great Bug Reports tend to have:

- A quick summary and/or background
- Steps to reproduce
- Be specific and give sample code if you can. Can also be Pseudocode.
- What you expected would happen
- What actually happens
- Notes (possibly including why you think this might be happening, or stuff you tried that didn't work)

**We Use Git-Flow for Code Contributions**

Pull requests are the best way to propose changes to the codebase. We use `Github Flow <https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow>`_. We actively welcome your pull requests:

1. Fork the repo and create your branch from `development`. The development branch always has the newest changes.
2. If you've added code that should be tested, add tests.
3. If you introduce new API also add clear documentation.
4. Ensure the test suite passes.
5. Make sure your code lints.
6. Issue that pull request!

**Use a Consistent Coding Style**

Have a close look to the existing code stile that is used within SEB Server and adapt to it as close as possible.
We reserve the right to adapt contributed code to the code style matching SEB Server code style before or after a pull request.

**Any contributions you make will be under the Mozilla Public License Version 2.0**

In short, when you submit code changes, your submissions are understood to be under the same `Mozilla Public License <https://github.com/SafeExamBrowser/seb-server?tab=MPL-2.0-1-ov-file>`_ that covers the project. Feel free to contact the maintainers if that's a concern.

**Becoming a SEB Alliance member**

The `SEB Alliance <https://www.safeexambrowser.org/alliance/members.html>`_ is the body which sustains ongoing funding of the Safe Exam Browser open source project to continue its maintenance, development and support activities. ETH Zurich provides the infrastructure for the management and the software engineering of the SEB project and appoints an alliance manager who will provide administrative support to the SEB Alliance, and ensure the day-to-day running of the SEB Alliance. ETH Zurich leads the Alliance and offers different contribution levels to parties interested in the evolution of the SEB open source project.

More information about `joining <https://www.safeexambrowser.org/alliance/join.html>`_ the Alliance is available in our `benefits <https://www.safeexambrowser.org/alliance/benefits.html>`_ and `documents <https://www.safeexambrowser.org/alliance/documents.html>`_ section.


